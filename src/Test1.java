import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
public class Test1 extends JFrame implements ActionListener, KeyListener{
    private static final int DEFAULT_SPEED = 5;//初始速度
    private static final int ACCELERATION = 2;//每次加速的增量
    private static final int MAX_SPEED = 20;//最大速度
    private int x = 650;
    private int y = 325;//车的初始位置
    private int speed1 = 5;//后期速度
    private int x1 = 150;
    private int y1 = 475;//车的初始位置
    private int speed2 = 5;//后期速度
    private ImageIcon carIcon; //用于显示第一辆车的图片
    private ImageIcon carIcon1;//用于显示第二辆车的图片
    private double angle = 0; // 表示汽车1的当前角度，单位为弧度
    private double angleIncrement = Math.PI / 12; // 每次转向的角度增量15度
    private double angle1 = 0; // 表示汽车2的当前角度，单位为弧度
    private double angleIncrement1 = Math.PI / 12;
    JTextArea wbqy1=new JTextArea();  //行驶数据记录的文本区
    JButton start=new JButton("启动");//启动灯组的按钮
    JButton stop=new JButton("暂停");//关闭灯组的按钮
    JScrollPane js = new JScrollPane();//设置滚动条
    //灯
    //北灯
    static light light_N_left=new light(80,0,light.red);
    static light light_N_middle=new light(40,0,light.green);
    static light light_N_right=new light(0,0,light.green);
    //南灯
    static light light_S_left=new light(40,0,light.red);
    static light light_S_middle=new light(80,0,light.green);
    static light light_S_right=new light(120,0,light.green);
    //西灯
    static light light_W_left=new light(0,40,light.red);
    static light light_W_middle=new light(0,80,light.red);
    static light light_W_right=new light(0,120,light.green);
    //东灯
    static light light_E_left=new light(0,80,light.red);
    static light light_E_middle=new light(0,40,light.red);
    static light light_E_right=new light(0,0,light.green);
    //四个灯的初始化
    light_control Light1=new  light_control(light_N_right,light_N_middle,light_N_left,"NS",'N',10);
    light_control Light2=new  light_control(light_S_right,light_S_middle,light_S_left,"NS",'S',10);
    light_control Light3=new  light_control(light_E_right,light_E_middle,light_E_left,"WE",'E',20);
    light_control Light4=new  light_control(light_W_right,light_W_middle,light_W_left,"WE",'W',20);
    //四个灯的线程
    Thread f1=new Thread(Light1);
    Thread f2=new Thread(Light2);
    Thread f3=new Thread(Light3);
    Thread f4=new Thread(Light4);
    public Test1(){
        setTitle("交通模拟系统");
        setLayout(null);
        setSize(800,800);
        //确定四个灯组的位置
        //北灯
        Light1.setBounds(245,477,160,40);
        //南灯
        Light2.setBounds(380,222,160,40);
        //东灯
        Light3.setBounds(245,222,40,160);
        //西灯
        Light4.setBounds(500,357,40,160);

        start.addActionListener(this);//为按钮设置动作监听器
        start.setBounds(60, 30, 80, 30);
        start.setFocusable(false);//转换焦点否则无法监听到键盘的按钮，无法处理车辆移动的事件

        stop.addActionListener(this);
        stop.setBounds(60, 70, 80, 30);
        stop.setFocusable(false);

        wbqy1.setBounds(550,550,230,200);

        js.setBounds(550,550,230,200);
        js.setViewportView(wbqy1);//创建一个视口，并在括号内设置其视图
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);//让垂直的滚动条一直处于显示状态


        //设置车辆图片的一些基本属性
        speed1 = DEFAULT_SPEED;
        speed2 = DEFAULT_SPEED;
        carIcon = new ImageIcon("src/图片/N2S(1).png");
        carIcon1 = new ImageIcon("src/图片/W2E2.png");
        int width = 50; // 设置初始宽度
        int height = 30; // 设置初始高度
        carIcon.setImage(carIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        carIcon1.setImage(carIcon1.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        addKeyListener(this);
        setFocusable(true); //焦点转换到键盘
        //添加到窗口中去
        this.add(start);
        this.add(stop);
        this.add(js);
        this.add(Light1);
        this.add(Light2);
        this.add(Light3);
        this.add(Light4);
        //设置窗口的初始位置
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    public void paint(Graphics g1){
        super.paint(g1);
        //1车相关事项提醒
        g1.setColor(Color.black);
        g1.setFont(new Font("宋体",Font.BOLD,20));
        g1.drawString("1车操作指南：\n",600,60);
        g1.drawString("\n∧ 1车前进",600,90);
        g1.drawString("\n∨ 1车后退",600,120);
        g1.drawString("\n< 1车向西旋转",600,150);
        g1.drawString("\n> 1车向东旋转",600,180);
        g1.drawString("\n1 1车加速行驶",600,210);
        g1.drawString("\n2 1车减速行驶",600,240);
        g1.drawString("行驶数据记录",600,570);
        //2车相关事项提醒
        g1.setColor(Color.black);
        g1.setFont(new Font("宋体",Font.BOLD,20));
        g1.drawString("2车操作指南：\n",60,580);
        g1.drawString("\nw 2车前进",60,610);
        g1.drawString("\ns 2车后退",60,640);
        g1.drawString("\na 2车向西旋转",60,670);
        g1.drawString("\nd 2车向东旋转",60,700);
        g1.drawString("\n3 2车加速行驶",60,730);
        g1.drawString("\n4 2车减速行驶",60,760);

        Stroke stroke=new BasicStroke(3.0f);//设置线宽为3.0
        ((Graphics2D) g1).setStroke(stroke);
        //温馨提示
        g1.setColor(Color.red);
        g1.setFont(new Font("宋体",Font.BOLD,20));
        g1.drawString("\n 虚拟游戏",60,180);
        g1.drawString("\n 请勿模仿",60,220);

        //绘画马路背景
        g1.setColor(Color.DARK_GRAY);
        g1.fillRect(550,250,300,300);
        g1.setColor(Color.DARK_GRAY);
        g1.fillRect(250,0,300,250);
        g1.setColor(Color.DARK_GRAY);
        g1.fillRect(0,250,250,300);
        g1.setColor(Color.DARK_GRAY);
        g1.fillRect(250,550,300,250);
        g1.setColor(Color.DARK_GRAY);
        g1.fillRect(250,550,300,250);
        g1.fillRect(250,250,300,300);
        //绘画马路中间黄线
        g1.setColor(Color.orange);
        g1.drawLine(400,0,400,250);
        g1.drawLine(400,550,400,800);
        g1.drawLine(0,400,250,400);
        g1.drawLine(550,400,800,400);
        //绘画白马路
        g1.setColor(Color.white);
        g1.drawLine(300,0,300,250);
        g1.drawLine(350,0,350,250);
        g1.drawLine(450,0,450,250);
        g1.drawLine(500,0,500,250);
        g1.drawLine(550,0,550,250);
        g1.drawLine(300,550,300,800);
        g1.drawLine(350,550,350,800);
        g1.drawLine(450,550,450,800);
        g1.drawLine(500,550,500,800);
        g1.setColor(Color.white);
        g1.drawLine(0,300,250,300);
        g1.drawLine(0,350,250,350);
        g1.drawLine(0,450,250,450);
        g1.drawLine(0,500,250,500);
        g1.drawLine(550,300,800,300);
        g1.drawLine(550,350,800,350);
        g1.drawLine(550,450,800,450);
        g1.drawLine(550,500,800,500);
        g1.setColor(Color.white);
        g1.drawLine(250,0,250,800);
        g1.drawLine(0,250,800,250);
        g1.drawLine(0,550,800,550);
        g1.setColor(Color.white);
        g1.drawLine(550,0,550,800);
        g1.setColor(Color.white);
        g1.drawLine(250,0,250,200);
        g1.drawLine(250,600,250,800);
        //画箭头
        g1.setColor(Color.white);
        g1.drawLine(150,475,200,475);
        g1.drawLine(190,460,200,475);
        g1.drawLine(190,490,200,475);
        g1.drawLine(150,425,200,425);
        g1.drawLine(200,425,200,400);
        g1.drawLine(190,415,200,400);
        g1.drawLine(200,400,210,415);
        g1.drawLine(150,525,200,525);
        g1.drawLine(200,525,200,550);
        g1.drawLine(210,535,200,550);
        g1.drawLine(200,550,190,535);
        //
        g1.drawLine(600,325,650,325);
        g1.drawLine(600,325,610,340);
        g1.drawLine(600,325,610,310);
        g1.drawLine(600,275,650,275);
        g1.drawLine(600,275,600,250);
        g1.drawLine(590,265,600,250);
        g1.drawLine(600,250,610,265);
        g1.drawLine(600,375,650,375);
        g1.drawLine(600,375,600,400);
        g1.drawLine(600,400,610,385);
        g1.drawLine(600,400,590,385);
        //
        g1.drawLine(475,600,475,650);
        g1.drawLine(475,600,490,610);
        g1.drawLine(475,600,460,610);
        g1.drawLine(525,600,525,650);
        g1.drawLine(525,600,550,600);
        g1.drawLine(550,600,535,610);
        g1.drawLine(550,600,535,590);
        g1.drawLine(425,600,425,650);
        g1.drawLine(425,600,400,600);
        g1.drawLine(400,600,415,590);
        g1.drawLine(400,600,415,610);
        //
        g1.drawLine(325,150,325,200);
        g1.drawLine(325,200,310,190);
        g1.drawLine(325,200,340,190);
        g1.drawLine(375,150,375,200);
        g1.drawLine(375,200,400,200);
        g1.drawLine(400,200,385,210);
        g1.drawLine(400,200,385,190);
        g1.drawLine(275,150,275,200);
        g1.drawLine(275,200,250,200);
        g1.drawLine(250,200,265,190);
        g1.drawLine(250,200,265,210);
        //绘画车辆，导入图片
        Graphics2D g2d = (Graphics2D) g1;
        g2d.rotate(angle, x, y);
        g2d.drawImage(carIcon.getImage(), x - carIcon.getIconWidth() / 2, y - carIcon.getIconHeight() / 2, null);
        //画笔是有方向的，然后你第1次旋转了n之后你想让它旋转，当前方向需要恢复初始方向，后面的旋转才可以正确旋转，才可以保证一辆车旋转
        //不会导致另一辆车旋转
        g2d.rotate(-angle, x, y);

        g2d.rotate(angle1, x1, y1);
        g2d.drawImage(carIcon1.getImage(), x1 - carIcon1.getIconWidth() / 2, y1 - carIcon1.getIconHeight() / 2, null);
    }
    public static void main(String[] args){
        new Test1().setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==start) {
            //启动四组灯
            light_control.RUN=true;
            f1.start();
            f2.start();
            f3.start();
            f4.start();
        }
        else if(e.getSource()==stop){
            light_control.RUN=false;
        }
    }//actionPerformed方法结束
    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }
    @Override
    public void keyPressed(KeyEvent e) {
        //车移动事件的监听
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                //1车前进方法
                moveBackward();
                //在文本区域显示相关内容
                String s1="1车前进：X="+x;
                String s2=" Y="+y+"\n";
                wbqy1.append(s1);
                wbqy1.append(s2);
                break;
            case KeyEvent.VK_DOWN:
                //1车后退方法
                moveForward();

                String s3="1车后退：X="+x;
                String s4=" Y="+y+"\n";
                wbqy1.append(s3);
                wbqy1.append(s4);
                break;
            case KeyEvent.VK_LEFT:
                //1车左转方法
                turnLeft();

                String s5="1车向西旋转：X="+x;
                String s6=" Y="+y+"\n";
                wbqy1.append(s5);
                wbqy1.append(s6);
                break;
            case KeyEvent.VK_RIGHT:
                //1车右转方法
                turnRight();

                String s7="1车向东旋转：X="+x;
                String s8=" Y="+y+"\n";
                wbqy1.append(s7);
                wbqy1.append(s8);
                break;
            case KeyEvent.VK_W:
                //2车前进方法
                moveForward1();

                String s11="2车前进：X="+x1;
                String s12=" Y="+y1+"\n";
                wbqy1.append(s11);
                wbqy1.append(s12);
                break;
            case KeyEvent.VK_S:
                //2车后退方法
                moveBackward1();

                String s13="2车后退：X="+x1;
                String s14=" Y="+y1+"\n";
                wbqy1.append(s13);
                wbqy1.append(s14);
                break;
            case KeyEvent.VK_A:
                //2车左转方法
                turnLeft1();

                String s15="2车向西旋转：X="+x1;
                String s16=" Y="+y1+"\n";
                wbqy1.append(s15);
                wbqy1.append(s16);
                break;
            case KeyEvent.VK_D:
                //2车右转方法
                turnRight1();
                String s17="2车向东旋转：X="+x1;
                String s18=" Y="+y1+"\n";
                wbqy1.append(s17);
                wbqy1.append(s18);
                break;
        }
        if (keyCode == KeyEvent.VK_NUMPAD1) { // '1' 键用于加速
            speed1 += ACCELERATION;
            if(speed1<MAX_SPEED) {
                String s1="1车加速\n";
                wbqy1.append(s1);
            }
            if (speed1 >= MAX_SPEED) {
                speed1 = MAX_SPEED;
                String s2="1车速度已达最高\n";
                wbqy1.append(s2);
            }
        } else if (keyCode == KeyEvent.VK_NUMPAD2) { // '2' 键用于减速
            speed1 -= ACCELERATION;
            if(speed1>0) {
                String s1="1车减速\n";
                wbqy1.append(s1);
            }
            if (speed1 <= 0) {
                speed1 = 0;
                String s2="1车速度已达最低\n";
                wbqy1.append(s2);
            }
        }
        if (keyCode == KeyEvent.VK_3) { // '3' 键用于加速
            speed2 += ACCELERATION;
            if(speed2<MAX_SPEED) {
                String s1="2车加速\n";
                wbqy1.append(s1);
            }
            if (speed2 >= MAX_SPEED) {
                speed2 = MAX_SPEED;
                String s2="2车速度已达最高\n";
                wbqy1.append(s2);
            }
        } else if (keyCode == KeyEvent.VK_4) { // '4' 键用于减速
            speed2 -= ACCELERATION;
            if(speed2>0) {
                String s1="2车减速\n";
                wbqy1.append(s1);
            }
            if (speed2 <= 0) {
                speed2 = 0;
                String s2="2车速度已达最低\n";
                wbqy1.append(s2);
            }
        }
        repaint();
    }
    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
    }
    private void moveForward() {
        x += Math.round(speed1 * Math.cos(angle));
        y += Math.round(speed1 * Math.sin(angle));
    }
    private void moveForward1() {
        x1 += Math.round(speed2 * Math.cos(angle1));
        y1 += Math.round(speed2 * Math.sin(angle1));
    }
    private void moveBackward() {
        x -= Math.round(speed1 * Math.cos(angle));
        y -= Math.round(speed1 * Math.sin(angle));
    }
    private void moveBackward1() {
        x1 -= Math.round(speed2 * Math.cos(angle1));
        y1 -= Math.round(speed2 * Math.sin(angle1));
    }
    private void turnLeft() {
        angle -= angleIncrement;
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
    }
    private void turnLeft1() {
        angle1 -= angleIncrement1;
        if (angle1 < 0) {
            angle1 += 2 * Math.PI;
        }
    }
    private void turnRight() {
        angle += angleIncrement;
        if (angle > 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
    }
    private void turnRight1() {
        angle1 += angleIncrement1;
        if (angle1 > 2 * Math.PI) {
            angle1 -= 2 * Math.PI;
        }
    }
}
class light{
    //灯的三种状态的指代，方便后续使用
    public static int yello=0;
    public static int red=1;
    public static int green=2;
    //储存灯的图片
    //0-黄 1-红 2-绿
    String light_img[]= {"src/图片/黄.png","src/图片/红.png","src/图片/绿.png"};
    //灯的位置
    int x,y;
    //灯的状态
    int state;
    public light(int x, int y, int state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }
    public String getphoto(){
        return this.light_img[state];
    }//得到灯得图片
}
class light_control extends Canvas implements Runnable{
    //灯的宽度
    public int width=40;
    //灯的高度
    public int height=40;
    //判断线程
    public static boolean RUN=true;
    light right;
    light middle;
    light left;
    //灯组的选择
    String choice_lightgroup;
    //灯的选择
    char choice_light;
    //灯的时间
    int time;
    public light_control(light right, light middle, light left,String choice_lightgroup, char choice_light, int time) {
        this.right = right;
        this.middle = middle;
        this.left = left;
        this.choice_lightgroup = choice_lightgroup;
        this.choice_light = choice_light;
        this.time = time;
    }
    //画灯
    //南北
    public void paint_ns(Graphics2D G){
        //因为图片数组返回的是字符串，所以要变成image变量，同时又要实例化用gettoolkit方法刚好
        G.drawImage(getToolkit().getImage(left.getphoto()),left.x,left.y,width,height,this);
        G.drawImage(getToolkit().getImage(middle.getphoto()),middle.x,middle.y,width,height,this);
        G.drawImage(getToolkit().getImage(right.getphoto()),right.x,right.y,width,height,this);
    }
    //东西
    public void paint_ew(Graphics2D G){
        //因为图片数组返回的是字符串，所以要变成image变量，同时又要实例化用gettoolkit方法刚好
        G.drawImage(getToolkit().getImage(left.getphoto()),left.x,left.y,width,height,this);
        G.drawImage(getToolkit().getImage(middle.getphoto()),middle.x,middle.y,width,height,this);
        G.drawImage(getToolkit().getImage(right.getphoto()),right.x,right.y,width,height,this);
    }
    public void paint(Graphics g){
        Graphics2D G=(Graphics2D) g;
        switch (this.choice_light){
            case 'E':
                paint_ew(G);
                //东计时板
                g.fillRect(left.x,left.y+40,width,height);
                g.setColor(Color.RED);
                G.setFont(new Font("宋体",Font.BOLD,40));
                g.drawString(time+" ",0,155);
                break;
            case 'W':
                paint_ew(G);
                //西计时板
                g.fillRect(0,0,width,height);
                g.setColor(Color.RED);
                G.setFont(new Font("宋体",Font.BOLD,40));
                g.drawString(time+" ",0,35);
                break;
            case 'N':
                paint_ns(G);
                //北计时板
                g.fillRect(120,0,width,height);
                g.setColor(Color.RED);
                G.setFont(new Font("宋体",Font.BOLD,40));
                g.drawString(time+" ",118,35);
                break;
            case 'S':
                paint_ns(G);
                //南计时板
                g.fillRect(0,0,width,height);
                g.setColor(Color.red);
                G.setFont(new Font("宋体",Font.BOLD,40));
                g.drawString(time+" ",0,37);
                break;
        }
    }
    @Override
    public void run() {
        String choice1="middle";
        String choice2="stop";
        while(RUN)
        {
            switch (choice_lightgroup) {
                case "NS":
                    switch (choice1) {
                        case "middle":
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            repaint();
                            this.time--;
                            if (this.time > 3) {
                                middle.state = light.green;
                                left.state = light.red;
                            } else if (this.time <= 3 && this.time > 0) {
                                middle.state = light.yello;
                                left.state = light.yello;
                            } else if (this.time == 0) {
                                this.time = 10;
                                middle.state = light.red;
                                left.state = light.green;
                                choice1="left";
                            }
                            break;
                        case "left":
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            repaint();
                            this.time--;
                            if (this.time > 3) {
                                middle.state = light.red;
                                left.state = light.green;
                            } else if (this.time <= 3 && this.time > 0) {
                                middle.state = light.yello;
                                left.state = light.red;
                            } else if (this.time == 0) {
                                this.time=20;
                                middle.state = light.red;
                                left.state = light.red;
                                choice1="stop";
                            }
                            break;
                        case "stop":
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            repaint();
                            this.time--;
                            if (this.time <= 3 && this.time > 0){
                                middle.state=light.yello;
                                left.state=light.red;
                            } else if (this.time==0) {
                                this.time=10;
                                middle.state=light.green;
                                left.state=light.red;
                                choice1="middle";
                            }
                            break;
                    }
                    break;
                case "WE":
                    switch(choice2) {
                        case "middle":
                        {
                            try {Thread.sleep(1000);}catch(InterruptedException e) {e.printStackTrace();}
                            repaint();
                            this.time--;
                            if(this.time>3) {
                                left.state=light.red;
                                middle.state=light.green;
                            }
                            if(this.time<=3&&this.time>0) {
                                left.state=light.yello;
                                middle.state=light.yello;
                            }
                            if(this.time==0) {
                                this.time=10;
                                left.state=light.green;
                                middle.state=light.red;
                                choice2="left";
                            }
                        }
                        break;
                        case "left":
                        {
                            try {Thread.sleep(1000);}catch(InterruptedException e) {e.printStackTrace();}
                            repaint();
                            this.time--;
                            if(time>3) {
                                left.state=light.green;
                                middle.state=light.red;
                            }
                            if(time>0&&time<=3) {
                                left.state=light.yello;
                                middle.state=light.red;
                            }
                            if(time==0) {
                                this.time=20;
                                left.state=light.red;
                                middle.state=light.red;
                                choice2="stop";
                            }
                        }
                        break;
                        case "stop":
                        {
                            try {Thread.sleep(1000);}catch(InterruptedException e) {e.printStackTrace();}
                            repaint();
                            this.time--;
                            if(this.time>3) {
                                left.state=light.red;
                                middle.state=light.red;//红

                            }
                            if(time>0&&time<=3) {
                                left.state=light.red;//红
                                middle.state=light.yello;//黄
                            }
                            if(this.time==0) {
                                time=10;
                                left.state=light.red;//红
                                middle.state=light.green;//绿
                                choice2="middle";
                            }
                        }
                        break;
                    }
                    break;
            }
        }
    }
}