/*
   a camera maintains the 
   viewing situation data

   (for simplicity and comfort,
    the "up" direction is [0,0,1],
    unchangeable)
*/

public class Camera{
  
  private double azimuth, altitude;
  private Triple eye;

  private double left, right, bottom, top, near, far;
  private Mat4 lookAt, project;

  public Camera( Triple e, double azi, double alt, 
                 double l, double r, double b, double t,
                 double n, double f ){

    eye = e;
    azimuth = azi;
    altitude = alt;

    left=l; right=r; bottom=b; top=t; near=n; far=f;

    updateView();
  }

  // update the matrices
  public void updateView(){

    System.out.println("update camera view with E=" + eye + 
                        " azi= " + azimuth + " alt= " + altitude +
                        " dist= " + near );
    double cos = Math.cos( Math.toRadians( azimuth ) );
    double sin = Math.sin( Math.toRadians( azimuth ) );
    double c2 = Math.cos( Math.toRadians( altitude ) );
    double s2 = Math.sin( Math.toRadians( altitude ) );

    lookAt = Mat4.lookAt( eye.x, eye.y, eye.z,   
       eye.x + near*cos*c2, eye.y + near*sin*c2, eye.z + near*s2,
       0, 0, 1 );

    project = Mat4.frustum( left, right, bottom, top, near, far );

  }

  // shift eye point by given vector
  public void shift( double dx, double dy, double dz ){
    eye = eye.add( new Triple( dx, dy, dz ) );
    updateView();
  }

  // change azimuth by given amount
  public void turn( double amount ){
    azimuth += amount;
    if( azimuth < 0 )
      azimuth += 360;
    if( azimuth > 360 )
      azimuth -= 360;
    updateView();
  }

  // change altitude by given amount
  public void tilt( double amount ){
    altitude += amount;
    if( altitude < 0 )
      altitude += 360;
    if( altitude > 360 )
      altitude -= 360;
    updateView();
  }

  // multiply distance by given factor
  public void zoom( double factor ){
    near *= factor;
    updateView();
  }

  public Mat4 getLookAt(){
    return lookAt;
  }

  public Mat4 getProject(){
    return project;
  }

}
