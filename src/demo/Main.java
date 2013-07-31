package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import render.SpaceViewPanel;
import render.XYTRenderNode;
import ships.Ship;
import demo.equipment.DemoEngine;
import demo.equipment.DemoSensor;
import demo.equipment.DemoStructure;
import env.Space;

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
	
	

	public static void main(String[] args) {
		final Ship ship = new Ship(1000, 200000);
		
		final DemoEngine torque1 = new DemoEngine(100, (float)(Math.PI/8), (float)(Math.PI/2), 100);
		final DemoEngine torque2 = new DemoEngine(100, (float)(-Math.PI/8), (float)(-Math.PI/2), 100);
		final DemoEngine torque3 = new DemoEngine(100, (float)(9 * Math.PI/8), (float)(Math.PI/2), 100);
		final DemoEngine torque4 = new DemoEngine(100, (float)(7 * Math.PI/8), (float)(-Math.PI/2), 100);
		
		final DemoEngine forward = new DemoEngine(30, (float)(Math.PI/2), 0, 100);
		final DemoEngine back = new DemoEngine(30, (float)(-Math.PI/2), 0, 100);
		final Space s = new Space();
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

		JFrame jf = new JFrame();
		jf.getContentPane().setLayout(new BorderLayout());
		
		final StatusBar bar;
		jf.add(bar = new StatusBar(), BorderLayout.SOUTH);
		jf.setVisible(true);
		jf.setSize(400, 400);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final SpaceViewPanel jp = new SpaceViewPanel(s);
		jp.addOverlay(new XYTRenderNode(10, 10, 0) {
			public void draw(Graphics2D g) {
				g.setColor(Color.yellow.darker());
				g.drawString(String.format("x:%.03f", ship.me.x), 10, 10);
				g.drawString(String.format("y:%.03f", ship.me.y), 10, 25);
				g.drawString(String.format("deg:%d", ((int)(ship.me.rot/Math.PI*180))%360), 10, 40);
				
				g.drawString(String.format("vx:%.03f", ship.me.xspeed()*33), 10, 55);
				g.drawString(String.format("vy:%.03f", ship.me.yspeed()*33), 10, 70);
				g.drawString(String.format("deg/s:%d", ((int)(ship.me.rotspeed()/Math.PI*180*33))%360), 10, 85);
			}
		});
		jf.getContentPane().add(jp, BorderLayout.CENTER);
		s.addEntity(ship);
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
				
				
				s.reset();
				return;
			}
		});
	}
}
