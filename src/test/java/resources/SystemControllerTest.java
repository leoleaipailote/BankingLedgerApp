package resources;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import dev.codescreen.controller.SystemController;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LocalDateTime.class })
public class SystemControllerTest {

    @Test
    public void testPingFailure() throws Exception {
        mockStatic(LocalDateTime.class);
        when(LocalDateTime.now()).thenThrow(new RuntimeException("Forced exception"));

        SystemController controller = new SystemController();
        try {
            controller.ping();
        } catch (RuntimeException ex) {
            assert (ex.getMessage().contains("Failed to process ping request"));
            // Further assertions can be made here
        }
    }
}
