/*
  a Triple holds [x,y,z], as a point or vector
  depending on context

  this class is immutable according to its 
  methods, but the instance variables are public,
  intended for read-only convenience
*/

import java.nio.FloatBuffer;

public class Triple
{
  public double x, y, z;

  public Triple( double xIn, double yIn, double zIn )
  {
    x = xIn;
    y = yIn;
    z = zIn;
  }

  public Triple( Triple other )
  {
    x = other.x;
    y = other.y;
    z = other.z;
  }

  public Triple( java.util.Scanner input )
  {
    x = input.nextDouble();
    y = input.nextDouble();
    z = input.nextDouble();
    input.nextLine();
  }

  public Vec4 toVec4(){
    return new Vec4( x, y, z, 1 );
  }

  public Triple vectorTo( Triple other )
  {
    return new Triple( other.x - x, other.y - y, other.z - z );
  }

  public Triple scalarProduct( double s )
  {
    return new Triple( s*x, s*y, s*z );
  }

  public double dotProduct( Triple other )
  {
    return x*other.x + y*other.y + z*other.z;
  }

  public Triple crossProduct( Triple other )
  {
    return new Triple( y*other.z - z*other.y,
                       z*other.x - x*other.z,
                       x*other.y - y*other.x );
  }

  // compute the point on the line from this point s of the way
  // along the vector d
  public Triple pointOnLine( double lambda, Triple d )
  {
    return new Triple( x + lambda*d.x, y + lambda*d.y, z + lambda*d.z );
  }

  // compute point lambda of the way from this point to q
  public Triple ofTheWay( double lambda, Triple q )
  {
    return new Triple( x + lambda*(q.x-x),
                       y + lambda*(q.y-y),
                       z + lambda*(q.z-z) );
  }

  public double norm()
  {
    return Math.sqrt( x*x + y*y + z*z );
  }

  // create a new triple this is a normalized version
  // of this triple
  public Triple normalized()
  {
    double len = norm();
    return new Triple( x/len, y/len, z/len );
  }

  // make a new triple that is this triple plus v
  public Triple add( Triple v )
  {
    return new Triple( x+v.x, y+v.y, z+v.z );
  }

  // make a new triple that is this triple minus v
  public Triple subtract( Triple v )
  {
    return new Triple( x-v.x, y-v.y, z-v.z );
  }

  // scale this triple 
  public Triple scale( double sx, double sy, double sz ){
    return new Triple( sx*x, sy*y, sz*z );
  }

  public String toString()
  {
    return "<" + x + " " + y + " " + z + ">";
  }

  // append this triple's data to buff
  public void copyData( FloatBuffer buff ){
    buff.put( (float) x );
    buff.put( (float) y );
    buff.put( (float) z );
  }

  // fill buff with this triple's data
  public void fillData( FloatBuffer buff ){
    buff.rewind();
    buff.put( (float) x );
    buff.put( (float) y );
    buff.put( (float) z );
    buff.rewind();
  }

  // since Triple is immutable, makes sense to have "constants"
  // ("final" probably doesn't do anything, since no method can change)
  public final static Triple zero = new Triple(0,0,0);
  public final static Triple xAxis = new Triple(1,0,0);
  public final static Triple yAxis = new Triple(0,1,0);
  public final static Triple zAxis = new Triple(0,0,1);

}
