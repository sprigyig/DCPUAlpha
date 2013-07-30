package physics;

public class BodyForce {
	private ForceSource source;

	public BodyForce(ForceSource source) {
		this.source = source;
	}

	public float torque() {
		return (float) -(Math.sin(source.alignment_theta()) * source.force_magnitude()) * source.position_radius();
	}

	public float forceX(float bodyTheta) {
		return (float) -(Math
				.cos(-bodyTheta - source.position_theta() - source.alignment_theta()) * source.force_magnitude());
	}

	public float forceY(float bodyTheta) {
		return (float) (Math
				.sin(-bodyTheta - source.position_theta() - source.alignment_theta()) * source.force_magnitude());
	}

}
