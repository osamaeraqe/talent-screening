package com.tracker.talent_screening;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for TalentScreeningApplication CommandLineRunner behavior.
 *
 * Testing stack:
 * - JUnit Jupiter (JUnit 5) for the test framework.
 * - Mockito for mocking (with deep stubs for Zeebe client's fluent API).
 * - Spring Boot's OutputCaptureExtension for capturing console/log output.
 */
@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class TalentScreeningApplicationTest {

    // Use deep stubs to accommodate the fluent API chain: newCreateInstanceCommand().bpmnProcessId(...).latestVersion()...
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ZeebeClient zeebeClient;

    private TalentScreeningApplication app;

    @BeforeEach
    void setUp() {
        app = new TalentScreeningApplication();
        // Inject zeebeClient into private field via reflection because the app uses field injection in production code
        injectZeebeClient(app, zeebeClient);
    }

    private static void injectZeebeClient(TalentScreeningApplication target, ZeebeClient client) {
        try {
            var f = TalentScreeningApplication.class.getDeclaredField("zeebeClient");
            f.setAccessible(true);
            f.set(target, client);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject ZeebeClient mock into TalentScreeningApplication", e);
        }
    }

    @Nested
    @DisplayName("run(...) happy path")
    class HappyPath {

        @Test
        @DisplayName("should start hr_upload process with expected variables and log instance key")
        void startsProcessWithExpectedVariablesAndLogs(CapturedOutput output) {
            // Arrange
            long expectedKey = 42L;
            ProcessInstanceEvent event = mock(ProcessInstanceEvent.class);
            when(event.getProcessInstanceKey()).thenReturn(expectedKey);

            // Stub the deep chain to return the mocked event on join()
            when(zeebeClient
                    .newCreateInstanceCommand()
                    .bpmnProcessId(eq("hr_upload"))
                    .latestVersion()
                    .variables(eq(Map.of("total", 100)))
                    .send()
                    .join())
                .thenReturn(event);

            // Act
            app.run();

            // Assert call order and parameters
            InOrder inOrder = Mockito.inOrder(zeebeClient,
                    zeebeClient.newCreateInstanceCommand(),
                    zeebeClient.newCreateInstanceCommand().bpmnProcessId("hr_upload"),
                    zeebeClient.newCreateInstanceCommand().bpmnProcessId("hr_upload").latestVersion()
            );

            // Verify the chain was invoked correctly
            inOrder.verify(zeebeClient).newCreateInstanceCommand();
            inOrder.verify(zeebeClient.newCreateInstanceCommand()).bpmnProcessId("hr_upload");
            inOrder.verify(zeebeClient.newCreateInstanceCommand().bpmnProcessId("hr_upload")).latestVersion();
            verify(zeebeClient.newCreateInstanceCommand().bpmnProcessId("hr_upload").latestVersion())
                    .variables(Map.of("total", 100));
            verify(zeebeClient.newCreateInstanceCommand().bpmnProcessId("hr_upload").latestVersion().variables(Map.of("total", 100)))
                    .send();
            verify(zeebeClient.newCreateInstanceCommand().bpmnProcessId("hr_upload").latestVersion().variables(Map.of("total", 100)).send())
                    .join();

            // Verify logging and stdout
            assertThat(output.getOut())
                    .as("Console output should contain greeting")
                    .contains("Hello Osama!");

            assertThat(output.getOut() + output.getErr())
                    .as("Logs should include the process instance key")
                    .contains("started a process instance")
                    .contains(String.valueOf(expectedKey));
        }

        @Test
        @DisplayName("should allow different process instance keys and still log correctly")
        void logsAnyProcessKey(CapturedOutput output) {
            // Arrange
            long key = 987654321L;
            ProcessInstanceEvent event = mock(ProcessInstanceEvent.class);
            when(event.getProcessInstanceKey()).thenReturn(key);
            when(zeebeClient
                    .newCreateInstanceCommand()
                    .bpmnProcessId(anyString())
                    .latestVersion()
                    .variables(anyMap())
                    .send()
                    .join())
                .thenReturn(event);

            // Act
            app.run();

            // Assert
            assertThat(output.getOut() + output.getErr())
                    .contains("started a process instance")
                    .contains(Long.toString(key));
        }
    }

    @Nested
    @DisplayName("run(...) failure scenarios")
    class Failures {

        @Test
        @DisplayName("should propagate runtime exceptions from Zeebe send/join")
        void propagatesExceptionsFromZeebe() {
            // Arrange
            when(zeebeClient
                    .newCreateInstanceCommand()
                    .bpmnProcessId(anyString())
                    .latestVersion()
                    .variables(anyMap())
                    .send()
                    .join())
                .thenThrow(new RuntimeException("zeebe failure"));

            // Act + Assert
            RuntimeException ex = assertThrows(RuntimeException.class, () -> app.run());
            assertThat(ex).hasMessageContaining("zeebe failure");
        }

        @Test
        @DisplayName("should still attempt to log when event has no key (defensive)")
        void handlesNullEventKeyGracefully(CapturedOutput output) {
            // Arrange: event returns null key
            ProcessInstanceEvent event = mock(ProcessInstanceEvent.class);
            when(event.getProcessInstanceKey()).thenReturn(0L); // using 0L to reflect missing key defensively
            when(zeebeClient
                    .newCreateInstanceCommand()
                    .bpmnProcessId(eq("hr_upload"))
                    .latestVersion()
                    .variables(eq(Map.of("total", 100)))
                    .send()
                    .join())
                .thenReturn(event);

            // Act
            app.run();

            // Assert: still logs the message; key 0L will appear
            assertThat(output.getOut() + output.getErr())
                    .contains("started a process instance")
                    .contains("0");
        }
    }

    @Nested
    @DisplayName("input/arguments handling")
    class ArgsHandling {

        @Test
        @DisplayName("should ignore provided args and still start process")
        void ignoresArgs(CapturedOutput output) {
            // Arrange
            ProcessInstanceEvent event = mock(ProcessInstanceEvent.class);
            when(event.getProcessInstanceKey()).thenReturn(1L);
            when(zeebeClient
                    .newCreateInstanceCommand()
                    .bpmnProcessId(anyString())
                    .latestVersion()
                    .variables(anyMap())
                    .send()
                    .join())
                .thenReturn(event);

            // Act - pass random args
            app.run("--foo", "bar");

            // Assert - behavior same as default run
            verify(zeebeClient.newCreateInstanceCommand()).bpmnProcessId("hr_upload");
            verify(zeebeClient.newCreateInstanceCommand().bpmnProcessId("hr_upload").latestVersion())
                    .variables(Map.of("total", 100));
            assertThat(output.getOut()).contains("Hello Osama!");
        }
    }
}