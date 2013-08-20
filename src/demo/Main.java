package demo;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import render.RenderPreferences;
import render.SpaceViewPanel;
import render.XYTRenderNode;
import ships.Equipment;
import ships.Ship;
import demo.equipment.DemoSensor;
import env.Asteroid;
import env.Space;
import equipment.Capacitor;
import equipment.Engine;
import equipment.Generator;
import equipment.PowerGrid;
import equipment.Structure;
import equipment.Synchronizer;

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
			if (!"".equals(text)) {
				timeout = new Timeout(5000);
				timeout.start();
			}
		}
	}
	
	

	public static void main(final Ship ship) {
		
		final PowerGrid grid = ship.power;
		final JFrame jf = new JFrame();
		jf.getContentPane().setLayout(new BorderLayout());
		final Space s = new Space();
		ship.in(s);
		
		final StatusBar bar;
		jf.add(bar = new StatusBar(), BorderLayout.SOUTH);
		jf.setVisible(true);
		jf.setSize(400, 400);
		
		final SpaceViewPanel jp = new SpaceViewPanel(s);
		jp.overlays().topLeft().addChild(new XYTRenderNode(10, 10, 0) {
			public void draw(Graphics2D g, RenderPreferences prefs) {
				g.setColor(prefs.overlayTextColor());
				g.drawString(String.format("x:%.03f", ship.me.x), 10, 10);
				g.drawString(String.format("y:%.03f", ship.me.y), 10, 25);
				g.drawString(String.format("deg:%d", ((int)(ship.me.rot/Math.PI*180))%360), 10, 40);
				
				g.drawString(String.format("vx:%.03f", ship.me.xspeed()*33), 10, 55);
				g.drawString(String.format("vy:%.03f", ship.me.yspeed()*33), 10, 70);
				g.drawString(String.format("deg/s:%d", ((int)(ship.me.rotspeed()/Math.PI*180*33))%360), 10, 85);
				
				g.drawString(String.format("Power: %4d", grid.getPower()), 10, 100);
			}
		});
		jf.getContentPane().add(jp, BorderLayout.CENTER);
		s.addEntity(ship);
		
		s.addEntity(new Asteroid(-100, -250, .01f, -0.05f, 60));
		
		s.start();
		
		jp.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1) {
					new Thread() {
						public void run() {
							bar.setStatus("");
							File f = null;
							JFileChooser jfc = new JFileChooser(new File("./"));
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
		
		
		bar.setStatus("Right Click to load DCPU Binary");
		jf.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {
			}
			
			public void windowIconified(WindowEvent e) {
			}
			
			public void windowDeiconified(WindowEvent e) {
			}
			
			public void windowDeactivated(WindowEvent e) {
			}
			
			public void windowClosing(WindowEvent e) {
				s.stop();
				jp.stop();
				jf.dispose();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public static void main(String[] args) {
		final Ship ship = new Ship(1000, 400000);
		
		final Equipment torque1 = new Engine(100, (float)(Math.PI/8), (float)(Math.PI/2), 100, 10, (char)1);
		final Equipment torque2 = new Engine(100, (float)(-Math.PI/8), (float)(-Math.PI/2), 100, 10, (char)2);
		final Equipment torque3 = new Engine(100, (float)(9 * Math.PI/8), (float)(Math.PI/2), 100, 10, (char)3);
		final Equipment torque4 = new Engine(100, (float)(7 * Math.PI/8), (float)(-Math.PI/2), 100, 10, (char)4);
		
		final Equipment forward = new Engine(50, (float)(Math.PI/2), 0, 100, 10, (char)5);
		final Equipment back = new Engine(50, (float)(-Math.PI/2), 0, 100, 10, (char)6);
		ship.addEquipment(torque1);
		ship.addEquipment(torque2);
		ship.addEquipment(torque3);
		ship.addEquipment(torque4);
		ship.addEquipment(forward);
		ship.addEquipment(back);
		ship.addEquipment(new DemoSensor((char)7));
		ship.addEquipment(new Synchronizer((char)8));
		ship.power.setHwid((char)9);
		ship.power.capacityAdded(1000);
		
		
		Structure struct;
		ship.addEquipment(struct = new Structure());
		ship.addEquipment(new Generator(0,0,0, 17));
		ship.addEquipment(new Capacitor(50, 0, 0));
		ship.addEquipment(new Capacitor(-50, 0, 0));
		for (int i=-2; i<=2; i++) struct.addLocation(i, 0);
		
		
		
		main(ship);
	}
	
	public static void reset(final Ship s, final char[] memory_contents) {
		
		s.cpu.runInCpuThread(new Runnable() {
			public void run() {
				s.cpu.memory.physical_memory = memory_contents;
				s.reset();
				s.power.setPower(s.power.getCapacity());
				return;
			}
		});
	}
}
