import java.util.Scanner;
import java.nio.FloatBuffer;

public class Mat4{

  public double[][] a;

  public Mat4( double a11, double a12, double a13, double a14,
               double a21, double a22, double a23, double a24,
               double a31, double a32, double a33, double a34,
               double a41, double a42, double a43, double a44 ){
    a = new double[4][4];
    a[0][0] = a11;  a[0][1] = a12;  a[0][2] = a13;  a[0][3] = a14;
    a[1][0] = a21;  a[1][1] = a22;  a[1][2] = a23;  a[1][3] = a24;
    a[2][0] = a31;  a[2][1] = a32;  a[2][2] = a33;  a[2][3] = a34;
    a[3][0] = a41;  a[3][1] = a42;  a[3][2] = a43;  a[3][3] = a44;
  }

  public Mat4( double[][] temp ){
    a = new double[4][4];
    for( int r=0; r<4; r++ )
      for( int c=0; c<4; c++ ){
        a[r][c] = temp[r][c];
      }
  }

  public Mat4( Scanner input ){
    a = new double[4][4];
    for( int r=0; r<4; r++ )
      for( int c=0; c<4; c++ ){
        a[r][c] = input.nextDouble();
      }
  }

  // create various handy matrices

  public static Mat4 identity(){
    return new Mat4( 1, 0, 0, 0,
                     0, 1, 0, 0,
                     0, 0, 1, 0,
                     0, 0, 0, 1 );
  }

  public static Mat4 translate( double a, double b, double c ){
    return new Mat4( 1, 0, 0, a,
                     0, 1, 0, b,
                     0, 0, 1, c,
                     0, 0, 0, 1 );
  }

  public static Mat4 rotate( double theta, double x, double y, double z ){
    double c = Math.cos( Math.toRadians( theta ) );
    double s = Math.sin( Math.toRadians( theta ) );
    Vec4 axis = new Vec4( x, y, z );
    axis = axis.normalize();

    x = axis.x[0];
    y = axis.x[1];
    z = axis.x[2];
    
    double d = 1-c;
    
    return new Mat4( x*x*d+c, x*y*d-s*z, x*z*d+s*y, 0,
                     x*y*d+s*z, y*y*d+c, y*z*d-s*x, 0,
                     x*z*d-s*y, y*z*d+s*x, z*z*d+c, 0,
                     0, 0, 0, 1 );
  }

  public static Mat4 scale( double a, double b, double c ){
    return new Mat4( a, 0, 0, 0,
                     0, b, 0, 0,
                     0, 0, c, 0,
                     0, 0, 0, 1 );
  }
 
  public static Mat4 frustum( double l, double r, double b, double t,
                               double n, double f ){
    return new Mat4( 2*n/(r-l), 0, (r+l)/(r-l), 0,
                     0, 2*n/(t-b), (t+b)/(t-b), 0,
                     0, 0, - (f+n)/(f-n), -(2*f*n)/(f-n),
                     0, 0, -1, 0 );
  }

  public static Mat4 lookAt( double eyex, double eyey, double eyez,
                             double cx, double cy, double cz,
                             double ux, double uy, double uz ){
    Vec4 e = new Vec4( eyex, eyey, eyez );
    Vec4 c = new Vec4( cx, cy, cz );
    Vec4 u = new Vec4( ux, uy, uz );
    
    Vec4 n = c.minus( e );
    Vec4 r = n.cross( u );
    Vec4 w = r.cross( n );
    n = n.normalize();
    r = r.normalize();
    w = w.normalize();
   
    Mat4 translate = new Mat4( 1, 0, 0, -e.x[0],
                               0, 1, 0, -e.x[1],
                               0, 0, 1, -e.x[2],
                               0, 0, 0, 1 );
    Mat4 rotate = new Mat4( r.x[0], r.x[1], r.x[2], 0,
                            w.x[0], w.x[1], w.x[2], 0,
                            -n.x[0], -n.x[1], -n.x[2], 0,
                            0, 0, 0, 1 );
    Mat4 lookAt = rotate.mult( translate );
    return lookAt;
  }

  // really uses 4D
  public Vec4 mult( Vec4 v ){
    double[] temp = new double[4];
    for( int r=0; r<4; r++ ){// compute row r of temp
      temp[r] = 0;
      for( int c=0; c<4; c++ ){
        temp[r] += a[r][c] * v.x[c];
      }
    }
    return new Vec4( temp );
  }

  public Mat4 mult( Mat4 m ){
    double[][] temp = new double[4][4];
    for( int r=0; r<4; r++ )
      for( int c=0; c<4; c++ ){
        // form temp[r][c]
        temp[r][c] = 0;
        for( int k=0; k<4; k++ )
          temp[r][c] += a[r][k] * m.a[k][c];
      }
    return new Mat4( temp );    
  }
  
  public String toString(){
    String s = "\n";
    for( int r=0; r<4; r++ ){
      for( int c=0; c<4; c++ ){
        s += Util.nice( a[r][c], 12, 5 );
      }
      s += "\n";
    }
    return s;
  }

  public static void main(String[] args){
    testFrustum();
    //testLookAt();
  }

  public static void testLookAt(){
/*
    Mat4 view = Mat4.lookAt( 3, 0, 0, 0, 0, 0, 0, 0, 1 );
    System.out.println( view );
    Vec4 p = new Vec4( 0, 1, 0 );
    System.out.println( view.mult( p ) );
*/
    Mat4 view = Mat4.lookAt( -1, 1, 0, 0, 2, 0, 0, 0, 1 );
    System.out.println( view );
    Vec4 p1 = new Vec4( 1, 3, 0 );
    Vec4 p2 = new Vec4( 2, 1, 0 );
    System.out.println( "p1 maps to: " +view.mult( p1 ) );
    System.out.println( "p2 maps to: " +view.mult( p2 ) );
  }
  
  public static void testFrustum(){
    Mat4 f = Mat4.frustum( 10, 20, 5, 12, 1, 11 );
    System.out.println("f=" + f );

    Vec4 nlb = new Vec4( 10, 5, -1 );
    Vec4 nrb = new Vec4( 20, 5, -1 );
    Vec4 nlt = new Vec4( 10, 12, -1 );
    Vec4 nrt = new Vec4( 20, 12, -1 );

    System.out.println("nlb: " + f.mult( nlb ).perspDiv() );
    System.out.println("nrb: " + f.mult( nrb ).perspDiv() );
    System.out.println("nlt: " + f.mult( nlt ).perspDiv() );
    System.out.println("nrt: " + f.mult( nrt ).perspDiv() );

    double fn = 11/1;

    Vec4 flb = new Vec4( fn*10, fn*5, -11 );
    Vec4 frb = new Vec4( fn*20, fn*5, -11 );
    Vec4 flt = new Vec4( fn*10, fn*12, -11 );
    Vec4 frt = new Vec4( fn*20, fn*12, -11 );

    System.out.println("flb: " + flb );
    System.out.println(" frustum * flb = " + f.mult( flb ) );
    System.out.println(" after p.d. " + f.mult( flb ).perspDiv() );

    System.out.println("flb: " + f.mult( flb ).perspDiv() );
    System.out.println("frb: " + f.mult( frb ).perspDiv() );
    System.out.println("flt: " + f.mult( flt ).perspDiv() );
    System.out.println("frt: " + f.mult( frt ).perspDiv() );
  }

  // convert this matrix a into handy column major
  // float[] and then make it a FloatBuffer
  public FloatBuffer toBuffer(){
    double[] da = new double[16];

    int index = 0;

    for( int c=0; c<4; c++ )
      for( int r=0; r<4; r++ )
      {
        da[index] = a[r][c];
        index++;
      }

    return Util.arrayToBuffer( da );
  }

}
