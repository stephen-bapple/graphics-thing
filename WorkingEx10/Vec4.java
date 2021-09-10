/*  implement general purpose 4-tuple,
    unless otherwise specified, the 4th (w)
    component is 1 and is not really used,
    doing what Triple did in our earlier work
*/

import java.util.Scanner;

public class Vec4{

  public double[] x;

  public Vec4( double a, double b, double c ){
    x = new double[4];
    x[0]=a;  x[1]=b;  x[2]=c;  x[3]=1;
  }

  public Vec4( double a, double b, double c, double d ){
    x = new double[4];
    x[0]=a;  x[1]=b;  x[2]=c;  x[3]=d;
  }

  public Vec4( Scanner input ){
    x = new double[4];
    for( int k=0; k<3; k++ ){
      x[k] = input.nextDouble();
    }
    x[3] = 1;
  }

  public Vec4( double[] temp ){
    x = new double[4];
    for( int k=0; k<4; k++ ){
      x[k] = temp[k];
    }
  }

  // this method cares about w
  public Vec4 perspDiv(){
    double w = x[3];
    return mult( 1/w );
  }

  public Vec4 mult( double a ){
    return new Vec4( a*x[0], a*x[1], a*x[2], a*x[3] );
  }

  public double dot( Vec4 other ){
    return x[0]*other.x[0] + x[1]*other.x[1] + x[2]*other.x[2];
  }

  public Vec4 cross( Vec4 other ){
    return new Vec4( x[1]*other.x[2]-other.x[1]*x[2],
                     x[2]*other.x[0]-other.x[2]*x[0],
                     x[0]*other.x[1]-other.x[0]*x[1], 0 );
  }

  public Vec4 normalize(){
    double len = Math.sqrt( this.dot( this ) );
    return this.mult( 1/len );   
  }

  public Vec4 minus( Vec4 other ){
    return new Vec4( x[0]-other.x[0], x[1]-other.x[1], x[2]-other.x[2], 0 );
  }

  public String toString(){
    return "[" + Util.nice(x[0],12,5) + "," + Util.nice(x[1],12,5) + "," +
                  Util.nice(x[2],12,5) + "," + Util.nice(x[3],12,5) + "]";
  }

  public Triple toTriple(){
    // comment out this check once checked
    if( x[3] != 1 ){
      System.out.println("invalid conversion of Vec4 to Triple");
      System.exit(1);
    }
    return new Triple( x[0], x[1], x[2] );
  }

}
