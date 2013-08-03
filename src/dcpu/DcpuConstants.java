package dcpu;

public class DcpuConstants {
	public static final int REG_A = 0;
	public static final int REG_B = 1;
	public static final int REG_C = 2;
	public static final int REG_X = 3;
	public static final int REG_Y = 4;
	public static final int REG_Z = 5;
	public static final int REG_I = 6;
	public static final int REG_J = 7;
	public static final int GP_REG_COUNT = 8;
	
	public static int INDIRECT(int reg) {
		return reg+0x8;
	}
	
	public static int INDIRECT_OFFSET(int reg) {
		return reg+0x10;
	}
	
	public static final int PUSH = 0x18;
	public static final int POP  = 0x18;
	public static final int PUSHPOP = 0x18;
	
	public static final int PEEK = 0x19;
	public static final int PICK = 0x1a;
	public static final int SP = 0x1b;
	public static final int PC = 0x1c;
	public static final int EX = 0x1d;
	public static final int LIT_IND = 0x1e;
	public static final int LONG_LIT = 0x1f;
	
	public static final int SHORT_LIT_MIN = -1;
	public static final int SHORT_LIT_MAX = 30;
	public static int SHORT_LIT(int lit) {
		return lit+0x21;
	}
	
	public static final int ADV = 0x00;
	public static final int SET = 0x01;
	public static final int ADD = 0x02;
	public static final int SUB = 0x03;
	public static final int MUL = 0x04;
	public static final int MLI = 0x05;
	public static final int DIV = 0x06;
	public static final int DVI = 0x07;
	public static final int MOD = 0x08;
	public static final int MDI = 0x09;
	public static final int AND = 0x0a;
	public static final int BOR = 0x0b;
	public static final int XOR = 0x0c;
	public static final int SHR = 0x0d;
	public static final int ASR = 0x0e;
	public static final int SHL = 0x0f;
	public static final int IFB = 0x10;
	public static final int IFC = 0x11;
	public static final int IFE = 0x12;
	public static final int IFN = 0x13;
	public static final int IFG = 0x14;
	public static final int IFA = 0x15;
	public static final int IFL = 0x16;
	public static final int IFU = 0x17;
	public static final int ADX = 0x1a;
	public static final int SBX = 0x1b;
	
	public static final int NOOP1=0x1c;
	public static final int NOOP2=0x1d;
	
	public static final int STI = 0x1e;
	public static final int STD = 0x1f;
	
	
	public static final int JSR = 0x01;
	
	public static final int ADVNOOP0=0x02;
	public static final int ADVNOOP1=0x03;
	public static final int ADVNOOP2=0x04;
	public static final int ADVNOOP3=0x05;
	public static final int ADVNOOP4=0x06;
	
	
	public static final int WFI = 0x07;
	public static final int INT = 0x08;
	public static final int IAG = 0x09;
	public static final int IAS = 0x0a;
	public static final int RFI = 0x0b;
	public static final int IAQ = 0x0c;
	
	public static final int ADVNOOP6=0x0c;
	public static final int ADVNOOP7=0x0d;
	public static final int ADVNOOP8=0x0e;
	public static final int ADVNOOP9=0x0f;
	
	public static final int HWN = 0x10;
	public static final int HWQ = 0x11;
	public static final int HWI = 0x12;
	
	
	public static final int ADVNOOPa=0x13;
	public static final int ADVNOOPb=0x14;
	public static final int ADVNOOPc=0x15;
	public static final int ADVNOOPd=0x16;
	public static final int ADVNOOPe=0x17;
	public static final int ADVNOOPf=0x18;
	public static final int ADVNOOP10=0x19;
	public static final int ADVNOOP11=0x1a;
	public static final int ADVNOOP12=0x1b;
	public static final int ADVNOOP13=0x1c;
	public static final int ADVNOOP14=0x1d;
	public static final int ADVNOOP15=0x1e;
	public static final int ADVNOOP16=0x1f;
	
	
	public static boolean IS_IF(int val) {
		return (val>=IFB && val <=IFU);
	}
	
	
	public static char ASSEMBLE(int op, int b, int a) {
		return (char)(op | (b << 5) | (a<<10));
	}
	

	public static int sign_extend(char c) {
		return (c & 0x8000) != 0 ? 0xFFFF0000 | c : c;
	}
	
	public static Dcpu mkcpu(int...init_memory) {
		Dcpu d = new Dcpu();
		
		for (int i=0; i<init_memory.length; i++) {
			d.memory.set((char)i, (char)init_memory[i]);
		}
		
		return d;
	}
	
	public static void main(String[] args) {
		System.out.printf("%04x\n",(int)ASSEMBLE(ADV, HWQ, SHORT_LIT(7)	));
	}
}
