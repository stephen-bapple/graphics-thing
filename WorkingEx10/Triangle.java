import java.util.Scanner;
import java.nio.FloatBuffer;

public class Triangle{

  private Triple v1, v2, v3;   // the vertices
  private Triple color;        // the solid color of the triangle

  public Triangle( Triple p1, Triple p2, Triple p3, Triple col ){
    // note:  copying references to same triple only works because
    //        Triple is immutable (assuming don't cheat and change
    //        instance data using publicness intended for read-only access)
    color = col;
    v1 = p1;
    v2 = p2;
    v3 = p3;
  }

  public Triangle( Scanner input ){
    color = new Triple( input );
    v1 = new Triple( input );
    v2 = new Triple( input );
    v3 = new Triple( input );
  }

  // copy this triangle's data into the buffers
  public void copyData( FloatBuffer posBuffer, FloatBuffer colBuffer ){
    
    v1.copyData( posBuffer );
    v2.copyData( posBuffer );
    v3.copyData( posBuffer );

    // for simplicity at this time all three vertices have same
    // color, but leave code ready to send different colors if want to later
    color.copyData( colBuffer );
    color.copyData( colBuffer );
    color.copyData( colBuffer );
  }

  public String toString(){
    return "{" + color + "[" + v1 + "   " + v2 + "   " + v3 + "]}";
  }

}
