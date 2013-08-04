package render;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import physics.RTTSource;

public class RenderTest {
	public static void main(String[] args) {
		JFrame jf = new JFrame();
		final JPanel jp = new JPanel() {
			private static final long serialVersionUID = -4949831575414066843L;

			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2 = (Graphics2D) g;
				
				XYTRenderNode rn = new XYTRenderNode(20, -20, 0) {
					public void draw(Graphics2D g, RenderPreferences prefs) {
						g.fillArc(-5, -5, 10, 10, 0, 360);
					}
				};
				
				
				RTTRenderNode rn2 = new RTTRenderNode(new RTTSource () {
					public float position_radius() {
						return 100;
					}

					public float position_theta() {
						return (float)- (Math.PI/4);
					}

					public float alignment_theta() {
						return (float) (Math.PI/4);
					}
					
				}) {
					public void draw(Graphics2D g, RenderPreferences prefs) {
						g.drawLine(0, 0, 40, 0);
					}
				};
				
				rn.addChild(rn2);
				
				XYTRenderNode root = new XYTRenderNode(0,0,0);
				root.addChild(rn);
				
				AffineTransform cart = new AffineTransform();
				cart.scale(1, -1);
				root.render(g2, cart, new StandardPrefs());
			}
		};
		
		jf.add(jp);
		jf.setVisible(true);
		jf.setSize(400,400);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		new Timer(30, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jp.repaint();
			}
		}).start();
	}
}
