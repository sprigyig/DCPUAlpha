package physics;


/*
 * Radius-Theta-Theta Source
 * Provides a relative position/rotation
 *         \       -'       
 *          \ T2 -'         
 *           \ -'         
 *           -'            
 *      R  -'                  
 *       -'                  
 *     -' T1                     
 *    O---------
 *    
 *   O: Origin
 *   T1: Position Theta
 *   T2: Alignment Theta
 *   R: Position Radius
 */
public interface RTTSource {
	public float position_radius();
	public float position_theta();
	public float alignment_theta();
}
