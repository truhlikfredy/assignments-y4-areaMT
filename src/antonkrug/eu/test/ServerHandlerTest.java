package antonkrug.eu.test;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import antonkrug.eu.ServerHandler;
import antonkrug.eu.ServerListenner;

@RunWith(Parameterized.class)
public class ServerHandlerTest {
  
  private ServerHandler   handler;
  private ServerListenner server;
  private Socket          client;
  private double          radius;
  private double          expected;

  
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { { 0, 0 }, { -1, 3.141 }, { 1, 3.141 },
        { 30, 2827.433 }, { 0.5642, 1.0 }, { 400000, 502654824574.366 },
        { -400000, 502654824574.366 } , { -0, 0 } });
  }
  
  
  public ServerHandlerTest(double radius, double expected) {
    this.radius   = radius;
    this.expected = expected;
  }
  
  
  @Before
  public void setUp() throws Exception {
    handler = new ServerHandler(server, client);
  }


  @Test
  public void testCalculatePi() {
    assertEquals("Area for radius "+radius, expected, handler.calculatePi(radius), 0.001);   
  }

}
