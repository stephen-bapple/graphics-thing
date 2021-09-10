public class ClownColor{

  public static Triple color( String s ){
    switch (s) {

      case "red": return new Triple( 1,0,0 ); 
      case "green": return new Triple( 0,1,0 ); 
      case "blue": return new Triple( 0,0,1 ); 

      case "yellow": return new Triple( 1,1,0 ); 
      case "magenta": return new Triple( 1,0,1 ); 
      case "cyan": return new Triple( 0,1,1 ); 

      case "orange": return new Triple( 1,0.5,0 ); 
      case "pink": return new Triple( 1,0,0.5 ); 

      case "chartreuse": return new Triple( 0.5,1,0 ); 
      case "lime": return new Triple( 0,1,0.5 ); 

      case "purple": return new Triple( 0.5,0,1 ); 
      case "turqoise": return new Triple( 0,0.5,1 ); 

      case "gray": return new Triple( 0.8, 0.8, 0.8 ); 

      default:  return new Triple(0,0,0); 
    }
  }

}
