package physics;

/*
 * XYT Source
 * Provides a relative position/rotation
 *    
 *             -'             
 *    |      -'                
 *    |    -' T                 
 *  Y |---+------                  
 *    |   |                  
 *    |   |                  
 *    O---------
 *        X    
 *   O: Origin
 *   X: position_x
 *   Y: position_y
 *   T: alignment_theta
 */
public interface XYTSource {
	public float position_x();
	public float position_y();
	public float alignment_theta();
}
