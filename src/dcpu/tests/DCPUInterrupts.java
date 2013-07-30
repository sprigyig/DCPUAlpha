package dcpu.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import dcpu.Dcpu;
import static dcpu.DcpuConstants.*;

public class DCPUInterrupts {
	@Test
	public void swi() {
		Dcpu d = mkcpu(
				ASSEMBLE(ADV, IAS, SHORT_LIT(4)),
				ASSEMBLE(ADV, IAQ, SHORT_LIT(0)),
				ASSEMBLE(ADV, INT, SHORT_LIT(10)),
				ASSEMBLE(SUB, PC, SHORT_LIT(1)),
				ASSEMBLE(SET, EX, REG_A),
				ASSEMBLE(ADV, RFI, SHORT_LIT(0))
		);
		
		d.step();// IAS 4
		assertEquals((char)4, d.regs.ia);
		assertEquals(1, d.cyclecnt);
		
		
		d.cyclecnt = 0;
		d.step();// IAQ 0, interrupts no longer queued
		assertEquals(false, d.queue_interrupts);
		
		
		d.cyclecnt = 0;
		d.step();//INT 10
		assertEquals(4, d.cyclecnt);
		assertEquals(false, d.interrupts.isEmpty());
		
		d.cyclecnt = 0;
		
		d.step();//One step phase for entering interrupt
		assertEquals((char)4, d.regs.pc);
		assertEquals(0, d.cyclecnt);
		assertEquals((char)10, d.regs.gp[REG_A]);
		
		d.step();//set EX, REG_A
		assertEquals((char)10, d.regs.ex);
		assertEquals(1, d.cyclecnt);
		
		d.cyclecnt = 0;
		d.step();//rfi
		assertEquals((char)0, d.regs.gp[REG_A]);
		assertEquals((char)3, d.regs.pc);
	}
	
	@Test
	public void iag() {
		Dcpu d = mkcpu(
				ASSEMBLE(ADV, IAG, REG_A)
		);
		d.regs.ia = (char) 10;
		d.step();
		assertEquals(1, d.cyclecnt);
		assertEquals((char)10, d.regs.gp[REG_A]);
	}
	
	
}
