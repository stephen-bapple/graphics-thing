/*
   hold a mesh of
   triangles
*/

import java.util.Scanner;
import java.nio.FloatBuffer;

public class TriMesh{

  private Triple[] points;  // points in space where vertices land

  private int[][] tris;  // each row has 3 indices of vertices in the triangle
 
  private Triple[] colors;  // color of each triangle

  public TriMesh( Scanner input ){
    int numVerts = input.nextInt();  input.nextLine();

    points = new Triple[ numVerts ];

    for( int k=0; k<points.length; k++ ){
      points[k] = new Triple( input );
    }

    int numTris = input.nextInt();  input.nextLine();

    tris = new int[numTris][3];
    colors = new Triple[numTris];

    for( int k=0; k<tris.length; k++ ){
      for( int j=0; j<3; j++ ){
        tris[k][j] = input.nextInt();
      }
      input.nextLine();
      colors[ k ] = ClownColor.color( input.next() );  input.nextLine();      
    }
   
  }
	// Construct a cylinder, maybe other (String, int, double, double) types later.
	public TriMesh(String kind, int numSides, double radius, double height, Scanner colorFile) {
		int numVerts = (numSides * 2) + 2; // Plus the center two
		int numTris = numSides * 4;
		int tri = 0; // Triangle counter
		
		double dTheta = 2 * Math.PI / numSides;
		double theta = 0.0;
		System.out.println("theta = " + theta + ", dTheta = " + dTheta);
		//colors = getRandomColors(numSides + 4, numTris);
		colors = new Triple[numTris];// Messed this up. needs to be NUM TRIANGLES.
		points = new Triple[numVerts];
		tris = new int[numTris][3];
		
		// The top and bottom center points are simple
		points[0] = new Triple(0,0,0); 
		points[1] = new Triple(0,0, height);
		int i = 2;
		
		// Loop through all the sides, rotating around the center point
		for (int k = 0; k < numSides; k++) {
			System.out.println("On side: " + k + ". Adding vertices " + i + " and " + (i + 1) + ".");
			System.out.println("total vertices: " + numVerts);
			System.out.println("total triangles: " + numTris);
			points[i] = new Triple(Math.cos(theta) * radius, Math.sin(theta) * radius, 0);
			points[i + 1] = new Triple(Math.cos(theta) * radius, Math.sin(theta) * radius, height);
			theta += dTheta; // Shouldn't ever have to reset
			
			// Side triangles
			Triple currentColor = getNextColor(colorFile);
			tris[tri][0] = i;
			tris[tri][1] = i + 1;
			tris[tri][2] = (i+2 < points.length) ? i + 2 : 0 + 2; 
			System.out.println("pointed to triangle " + tri);
			colors[tri] = currentColor;
			++tri;
			tris[tri][0] = (i+2 < points.length) ? i + 2 : 0 + 2;
			tris[tri][1] = (i+3 < points.length) ? i + 3 : 1 + 2;
			tris[tri][2] = i + 1; // Unsure about this
			System.out.println("pointed to triangle " + tri);
			colors[tri] = currentColor;
			++tri;
			
			// Bottom and top triangles
			currentColor = getNextColor(colorFile);
			tris[tri][0] = 0;
			tris[tri][1] = i;
			tris[tri][2] = (i+2 < points.length) ? i + 2 : 0 + 2;  
			System.out.println("pointed to triangle " + tri + " at " + ((i+2 < points.length) ? i + 2 : 0 + 2) + " shouldn't be higher than " + numVerts);
			System.out.println("i is" + i);
			colors[tri] = currentColor;
			++tri;
			currentColor = getNextColor(colorFile);
			tris[tri][0] = 1;
			tris[tri][1] = i + 1;
			tris[tri][2] = (i+3 < points.length) ? i + 3 : 1 + 2;
			System.out.println("pointed to triangle " + tri + " at " + ((i+3 < points.length) ? i + 3 : 1 + 2) + " shouldn't be higher than " + numVerts);
			System.out.println("i is" + i);
			colors[tri] = currentColor;
			++tri;
			
			i += 2;
		}
		
		System.out.println(points);
		System.out.println(colors);
	}
	
	// Construct a wall, maybe others later
	public TriMesh(String kind, int nw, int nh, Triple color1, Triple color2) {
		int numVerts = nh * nw * 2 * 3;  //input.nextLine();
		double dx = 1.0, dy = 1.0;
		int numTris = numVerts / 3;  //input.nextLine();
		tris = new int[numTris][3];
		
		colors = new Triple[numTris];

		points = new Triple[ numVerts ];
		double x = 0, y = 0;
		int k = 0; // vertex k
		int tri = 0; // Triangle number
		Triple currentColor = color1;
		
		for ( int row = 0; row < nh; row++) {
			for (int col = 0; col < nw; col++) {
				// Tri 1
				points[k] = new Triple(x, y, 0);
				points[k+1] = new Triple(x+dx, y, 0);
				points[k+2] = new Triple(x+dx, y + dy, 0);
				
				tris[tri][0] = k;
				tris[tri][1] = k + 1;
				tris[tri][2] = k + 2;
				//currentColor = ((row + col) % 2 ==0) ? color1 : color2;
				colors[tri] = ((row + col) % 2 ==0) ? color1 : color2;
				
				tri++;
				
				// Tri 2
				points[k+3] = points[k];
				points[k+4] = points[k+2];
				points[k+5] = new Triple(x, y + dy, 0);
				
				tris[tri][0] = k + 3;
				tris[tri][1] = k + 4;
				tris[tri][2] = k + 5;
				
				colors[tri] = ((row + col) % 2 ==0) ? color1 : color2;
				//currentColor = ((row + col) % 2 ==0) ? color1 : color2;
				tri++;
				
				// Increment counters
				k += 6; // Read six points.
				x +=dx;
			}
			// Move to the start of the next row up.
			x = 0.0;
			//currentColor = color1;
			y += dy;
		}
/* Deprecated \?
		for( int k=0; k<tris.length; k++ ){
			for( int j=0; j<3; j++ ){
				tris[k][j] = input.nextInt();
			}
			input.nextLine();
			colors[ k ] = ClownColor.color( input.next() );  input.nextLine();      
		}
*/ // Deprecated ^^^^
	}
	
  public void copyData( FloatBuffer posBuffer, FloatBuffer colorBuffer ){
    
    for( int k=0; k<tris.length; k++ ){
      for( int j=0; j<3; j++ ){
        points[ tris[k][j] ].copyData( posBuffer );
        colors[ k ].copyData( colorBuffer );
      }
    }

  }

  public int getNumTris(){
    return tris.length;
  }

  public void copyTransformedData( Mat4 transform,
                        FloatBuffer posBuffer, FloatBuffer colorBuffer ){

    Triple[] transformedPoints = new Triple[ points.length ];
    for( int k=0; k<transformedPoints.length; k++ ){
      transformedPoints[k] = transform.mult( points[k].toVec4() ).toTriple();
    }

    for( int k=0; k<tris.length; k++ ){
      for( int j=0; j<3; j++ ){
        transformedPoints[ tris[k][j] ].copyData( posBuffer );
        colors[ k ].copyData( colorBuffer );
      }
    }
    
  }

  public String toString(){
    String s = "";
    for( int k=0; k<points.length; k++ ){
      s += points[k];
    }
    return s;
  }
  
  private Triple getNextColor(Scanner colorFile) {
	  return ClownColor.color(colorFile.next());
  }
  
  private Triple[] getRandomColors(int arraySize) {
	  Triple[] colors = new Triple[arraySize];
	  for(int i = 0; i< arraySize-1; i+=2) {
		  colors[i] = new Triple(1,0,0);
		  colors[i+1] = new Triple(0,0,1);
	  }
	  
	  return colors;
  }

}
