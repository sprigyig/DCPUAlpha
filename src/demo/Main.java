package demo;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.*;

import javax.swing.*;

import demo.equipment.*;

import render.*;
import ships.*;

public class Main {
	private static class StatusBar extends JPanel {
		private static final long serialVersionUID = 1L;
		private JLabel label;
		private Timeout timeout;
		
		private class Timeout extends Thread {
			private long delay;
			private boolean canceled;
			
			public Timeout(long delay) {
				this.delay = delay;
			}
			public void run() {
				try {
					sleep(this.delay);
					synchronized(this) {
						if (!this.canceled) {
							setStatus("");
						}
					}
				} catch (InterruptedException e) {
				}
			}
			public void cancel() {
				synchronized(this) {
					canceled = true;
				}
			}
		}
		
		public StatusBar() {
			super(new BorderLayout());
			add(label = new JLabel(), BorderLayout.CENTER);
		}
		public void setStatus(String text) {
			label.setText(text);
			if (timeout!=null) {
				timeout.cancel();
			}
			timeout = new Timeout(5000);
			timeout.start();
		}
	}
	
	private static class Viewport extends XYTRenderNode {
		private double zoom;
		public Viewport(double x, double y, double theta, double zoom) {
			super(x, y, theta);
			this.zoom = zoom;
		}
		public void transform(AffineTransform at) {
			super.transform(at);
			at.scale(zoom, zoom);
		}
		public void zoom(double amt, double x, double y) {
			double oldworldx = (x-this.x)/zoom;
			double oldworldy = (y-this.y)/zoom;
			zoom += amt * (zoom/10);
			double newworldx = (x-this.x)/zoom;
			double newworldy = (y-this.y)/zoom;
			
			
			this.x -= (oldworldx-newworldx)*zoom;
			this.y -= (oldworldy-newworldy)*zoom;
	
		}
		public void moveCenter(double x, double y) {
			this.x -= x;
			this.y -= y;
		}
	}
	
	private static class ViewportMouseDrag implements MouseListener, MouseMotionListener {
		int lx, ly;
		private Viewport vp;
		
		public ViewportMouseDrag(Viewport vp) {
			this.vp = vp;
			lx = ly = -1;
		}
		
		public void mouseDragged(MouseEvent e) {
			if (lx!=-1) {
				vp.moveCenter(lx-e.getX(), ly+e.getY());
				lx = e.getX();
				ly = -e.getY();
			}
		}

		public void mouseMoved(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				lx = e.getX();
				ly = -e.getY();
			}
		}

		public void mouseReleased(MouseEvent e) {
			ly = lx = -1;
		}
		
	}
	public static void main(String[] args) {
		final Ship ship = new Ship(50, 20000);
		
		final DemoEngine torque1 = new DemoEngine(100, (float)(Math.PI/8), (float)(Math.PI/2), 100);
		final DemoEngine torque2 = new DemoEngine(100, (float)(-Math.PI/8), (float)(-Math.PI/2), 100);
		final DemoEngine torque3 = new DemoEngine(100, (float)(9 * Math.PI/8), (float)(Math.PI/2), 100);
		final DemoEngine torque4 = new DemoEngine(100, (float)(7 * Math.PI/8), (float)(-Math.PI/2), 100);
		
		final DemoEngine forward = new DemoEngine(30, (float)(Math.PI/2), 0, 100);
		final DemoEngine back = new DemoEngine(30, (float)(-Math.PI/2), 0, 100);
		
		ship.addEquipment(torque1);//1
		ship.addEquipment(torque2);//2
		ship.addEquipment(torque3);//3
		ship.addEquipment(torque4);//4
		ship.addEquipment(forward);//5
		ship.addEquipment(back);//6
		ship.addEquipment(new DemoSensor());//7
		
		ship.addEquipment(new DemoStructure(0, 0, 0));
		ship.addEquipment(new DemoStructure(-50, 0, 0));
		ship.addEquipment(new DemoStructure(-100, 0, 0));
		ship.addEquipment(new DemoStructure(50, 0, 0));
		ship.addEquipment(new DemoStructure(100, 0, 0));
		
//		ship.cpu.memory.physical_memory[0] = ASSEMBLE(ADV, HWI, SHORT_LIT(7));
//		ship.cpu.memory.physical_memory[1] = ASSEMBLE(SET, REG_A, SHORT_LIT(-1));
//		ship.cpu.memory.physical_memory[2] = ASSEMBLE(ADV, HWI, SHORT_LIT(1));
//		ship.cpu.memory.physical_memory[3] = ASSEMBLE(ADV, HWI, SHORT_LIT(2));
		

		final Viewport vp = new Viewport(200, -200, 0, 1);
		vp.addChild(ship.getVisuals());
		JFrame jf = new JFrame();
		jf.getContentPane().setLayout(new BorderLayout());
		final JPanel jp;
		jf.add(jp = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g) {
				int gray = 65;
				setBackground(new Color(gray,gray,(int)(gray*1.3)));
				super.paint(g);
				Graphics2D g2 = (Graphics2D) g;
				
				
				RenderNode rootWindow = new XYTRenderNode(0,0,0);
				
				rootWindow.addChild(vp);
				rootWindow.addChild(new RenderNode() {
					public void draw(Graphics2D g) {
						g.setColor(Color.yellow.darker());
						g.drawString(String.format("x:%.03f", ship.me.x), 10, 10);
						g.drawString(String.format("y:%.03f", ship.me.y), 10, 25);
						g.drawString(String.format("deg:%d", ((int)(ship.me.rot/Math.PI*180))%360), 10, 40);
						
						g.drawString(String.format("vx:%.03f", ship.me.xspeed()*33), 10, 55);
						g.drawString(String.format("vy:%.03f", ship.me.yspeed()*33), 10, 70);
						g.drawString(String.format("deg/s:%d", ((int)(ship.me.rotspeed()/Math.PI*180*33))%360), 10, 85);
					}
					protected void transform(AffineTransform root) {
						root.scale(1, -1);
					}
				});
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
				AffineTransform cart = new AffineTransform();
				cart.scale(1, -1);
				rootWindow.render(g2,cart);
				
			}
		}, BorderLayout.CENTER);
		final StatusBar bar;
		jf.add(bar = new StatusBar(), BorderLayout.SOUTH);
		jf.setVisible(true);
		jf.setSize(400, 400);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		new Timer(30, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ship.stepPhysics();
				ship.stepCpu(100000/30);
				jp.repaint();
			}
		}).start();
		
		jp.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				
			}
			
			public void mousePressed(MouseEvent e) {
				
			}
			
			public void mouseExited(MouseEvent e) {
				
			}
			
			public void mouseEntered(MouseEvent e) {
				
			}
			
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1) {
					new Thread() {
						public void run() {
							bar.setStatus("");
							File f = null;
							JFileChooser jfc = new JFileChooser();
							jfc.showOpenDialog(jp);
							f = jfc.getSelectedFile();
							
							if (f!=null && f.exists()) {
								try {
									FileInputStream fis = new FileInputStream(f);
									char[] image = new char[0x10000];
									for (int i=0;i<image.length;i++) image[i] = 0;
									int octetIndex = 0;
									
									byte[] buffer = new byte[1024];
									int read = fis.read(buffer);
									while (read != -1) {
										for (int i=0;i<read;i++) {
											if (octetIndex/2 < image.length) {
												if (octetIndex % 2 == 0) {
													image[octetIndex/2] = (char) (buffer[i]<<8);
												} else {
													image[octetIndex/2] = (char)(image[octetIndex/2] | (((int)buffer[i])&0xFF));
												}
												octetIndex+=1;
											}
										}
										read = fis.read(buffer);
									}
									for (int i =0;i<30;i++) {
										System.out.printf("%03d:%04x\n", i,(int)image[i]);
									}
									reset(ship, image);
									fis.close();
									
								} catch (IOException e) {
									bar.setStatus("Unable to read "+f.getAbsolutePath());
								}
							} else if (f!=null){
								bar.setStatus(f.getAbsolutePath()+" does not exist");
							}
							
						}
					}.start();
				}
			}
		});
		
		jp.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				vp.zoom(-e.getPreciseWheelRotation(), e.getX(), -e.getY());
			}
		});
		ViewportMouseDrag drag = new ViewportMouseDrag(vp);
		jp.addMouseMotionListener(drag);
		jp.addMouseListener(drag);
		bar.setStatus("Right Click to load DCPU Binary");
	}
	public static void reset(final Ship s, final char[] memory_contents) {
		
		s.cpu.runInCpuThread(new Runnable() {
			public void run() {
				s.cpu.memory.physical_memory = memory_contents;
				s.cpu.regs.pc = 0;
				s.cpu.regs.ex = 0;
				s.cpu.regs.sp = 0;
				s.cpu.regs.ia = 0;
				for (int i=0;i<s.cpu.regs.gp.length;i++) {
					s.cpu.regs.gp[i]=0;
				}
				
				s.me.rotnrg = 0f;
				s.me.xnrg = 0f;
				s.me.ynrg = 0f;
				s.me.x = 0f;
				s.me.y = 0f;
				s.me.rot = 0f;
				s.cpu.debug = 30;
				return;
			}
		});
	}
}
