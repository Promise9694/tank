package 坦克;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Vector;

public class 坦克1 extends JFrame implements ActionListener {
    MyPanel my=null;
    //定义一个开始面板
    MyStartPanel msp=null;
    //做出我需要的菜单
    JMenuBar jmb=null;
    //开始游戏
    JMenu jm1=null;
    JMenuItem jmi1=null;
    //退出系统
    JMenuItem jmi2=null;
    //存盘退出
    JMenuItem jmi3=null;
    JMenuItem jmi4=null;
    public static void main(String[] args){
        坦克1 ch=new 坦克1();
    }
    //构造函数
    public 坦克1()
    {
        //创建菜单及菜单选项
        jmb=new JMenuBar();
        jm1=new JMenu("游戏(G)");
        //设置快捷方式
        jm1.setMnemonic('G');
        jmi1=new JMenuItem("开始新游戏(N)");
        jmi1.setMnemonic('N');
        jmi2=new JMenuItem("退出游戏(E)");
        jmi2.setMnemonic('E');
        jmi3=new JMenuItem("存盘退出游戏(C)");
        jmi3.setMnemonic('C');
        jmi4=new JMenuItem("继续游戏(S)");
        jmi4.setMnemonic('S');

        jm1.add(jmi1);
        jm1.add(jmi2);
        jm1.add(jmi3);
        jm1.add(jmi4);


        jmb.add(jm1);

        msp=new MyStartPanel();
        Thread t=new Thread(msp);
        t.start();

        //对jmi1响应
        jmi1.addActionListener(this);
        jmi2.addActionListener(this);
        jmi1.setActionCommand("开始新游戏");
        jmi2.setActionCommand("退出游戏");
        jmi3.addActionListener(this);
        jmi3.setActionCommand("存档");
        jmi4.addActionListener(this);
        jmi4.setActionCommand("继续");
        this.setJMenuBar(jmb);
        this.add(msp);

        this.setSize(1000,800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocation(300,10);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //对用户不同的点击作出不同的处理
        if (e.getActionCommand().equals("开始新游戏"))
        {
            my=new MyPanel();
            Thread t=new Thread(my);
            t.start();
            //先删除旧的面板，再添加新的面板
            this.remove(msp);
            this.add(my);
            this.addKeyListener(my);
            //显示
            this.setVisible(true);
        }else if (e.getActionCommand().equals("退出游戏"))
        {
            Recorder.KeepInfo();
            System.exit(0);
        }else if(e.getActionCommand().equals("存档"))
        {
            Recorder rd=new  Recorder();
            rd.setEts(my.ets);
            rd.KeepEnmy();
            System.exit(0);
        }else if(e.getActionCommand().equals("继续"))
        {

        }
    }
}

class MyStartPanel extends JPanel implements Runnable
{
    int times=0;
    public void paint(Graphics g)
    {
        super.paint(g);
        g.fillRect(0,0,800,605);
        if (times%2==0) {
            //提示信息
            g.setColor(Color.yellow);
            //开关信息的字体
            Font mf = new Font("华文新魏", Font.BOLD, 60);
            g.setFont(mf);
            g.drawString("stage：1", 300, 250);
        }
    }

    @Override
    public void run() {
        while (true)
        {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (times<=20) {
                times++;
            }else {
                times=0;
            }
            //重画
            this.repaint();
        }
    }
}

//我的面板
class MyPanel extends JPanel implements KeyListener,Runnable
{
    boolean stop=false;
    //定义一个我的坦克
    Hero hero=null;
    //判断是继续还是新游戏
    String flag="newGame";
    //定义敌人的坦克组
    Vector<EnemyTank> ets=new Vector<EnemyTank>();

    //定义炸弹集合
    Vector<Boom> booms=new Vector<Boom>();
    Image[] images=new Image[7];

    int enSize=15;
    //定义爆炸图片
    Image image1=null;
    Image image2=null;
    Image image3=null;
    Image image4=null;
    Image image5=null;
    Image image6=null;
    Image image7=null;

    //构造函数
    public MyPanel()
    {
        //恢复记录
        Recorder.getSave();

        hero=new Hero(400,300,5);
        Thread k=new Thread(hero);
        k.start();
//        Boom b1=new Boom(800,10);//这里的坐标随意设的
//        booms.add(b1);
        if (this.flag.equals("newGame")) {
            //初始化敌人坦克
            for (int i = 0; i < enSize; i++) {
                //创建一辆敌人的坦克对象
                EnemyTank et = new EnemyTank((i + 1) * 50, 0);
                et.setColor(2);
                et.setDir(2);
                //将MyPanel的敌人坦克向量交给该敌人坦克
                et.setEts(ets);

                //启动敌人坦克
                Thread t = new Thread(et);
                t.start();

                //给敌人坦克添加一颗子弹
                Shot s = new Shot(et.x + 10, et.y + 30, et.dir, 5);
                //子弹加入给敌人
                et.ss.add(s);
                //启动子弹线程
                Thread t1 = new Thread(s);
                t1.start();
                //加入
                ets.add(et);
            }
        }
        try {
            image1=ImageIO.read(this.getClass().getResource("/bom1.png"));
            image2=ImageIO.read(this.getClass().getResource("/boom2.png"));
            image3=ImageIO.read(this.getClass().getResource("/boom3.png"));
            image4=ImageIO.read(this.getClass().getResource("/boom4.png"));
            image5=ImageIO.read(this.getClass().getResource("/boom6.png"));
            image6=ImageIO.read(this.getClass().getResource("/boom7.png"));
            image7=ImageIO.read(this.getClass().getResource("/boom8.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        //初始化图片
//        image1=Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("/bom1.png"));
//        image2=Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("/boom2.png"));
//        image3=Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("/boom3.png"));
//        image4=Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("/boom4.png"));
//        image5=Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("/boom6.png"));
//        image6=Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("/boom7.png"));
//        image7=Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("/boom8.png"));

    }
    //画出提示信息
    public void showInfo(Graphics g)
    {
        this.drawTank(500,650,g,0,1);
        g.setColor(Color.black);
        g.drawString(Recorder.getEnNum()+"",530,680);
        this.drawTank(580,650,g,0,0);
        g.setColor(Color.black);
        g.drawString(Recorder.getMylife()+"",610,680);

        //画出玩家总成绩
        g.setColor(Color.black);
        g.setFont(new Font(" 宋体 ",Font.BOLD,20));
        g.drawString(" 您的总成绩 ", 820, 30);

        this. drawTank(820, 60, g, 1, 0);
        g.setColor(Color.black);
        g.setFont(new Font(" 宋体 ",Font.BOLD,20));
        g.drawString(Recorder.getAllEnNum()+"", 860, 80);
    }
    //重写paint方法
    public void paint(Graphics g)
    {
        super.paint(g);
        g.fillRect(0,0,800,600);

        //画出提示信息
        this.showInfo(g);

        //画出自己的坦克
        if(hero.is) {
            this.drawTank(hero.getX(), hero.getY(), g, this.hero.dir, 0);
        }
        //从ss中取出每颗子弹并画出
        for (int i=0;i<this.hero.ss.size();i++)
        {
            Shot shot=hero.ss.get(i);
            if (shot != null&& shot.is) {
                g.draw3DRect(shot.x, shot.y, 1, 1, false);
            }
            if (!shot.is)
           {
               //从ss中删掉该子弹
               hero.ss.remove(shot);
           }
        }
        //画出炸弹
        for (int l=0;l<booms.size();l++)
        {
            Boom b=booms.get(l);
            if(b.life>=19&b.life<=21){
                g.drawImage(image1,b.x,b.y,30,30,this);
            }else if(b.life>=16&b.life<=18){
            g.drawImage(image2,b.x,b.y,30,30,this);
            }else if(b.life>=13&b.life<=15){
                g.drawImage(image3,b.x,b.y,30,30,this);
            }else if(b.life>=11&b.life<=13){
                g.drawImage(image4,b.x,b.y,30,30,this);
            }else if(b.life>=8&b.life<=10){
                g.drawImage(image5,b.x,b.y,30,30,this);
            }else if(b.life>=5&b.life<=7){
                g.drawImage(image6,b.x,b.y,30,30,this);
            }else if(b.life>0&b.life<=4){
                g.drawImage(image7,b.x,b.y,30,30,this);
            }

            b.lifeDown();
            //如果炸弹生命值为零，就把炸弹从booms向量中去掉
            if (b.life==0)
            {
                booms.remove(b);
            }
        }
        //画出敌人的坦克
        for (int i=0;i<ets.size();i++)
        {
            EnemyTank et=ets.get(i);
            int f=(int) (Math.random()*4);
            if (et.is) {
                this.drawTank(et.getX(), et.getY(), g, et.getDir(), f);
                //在画出敌人的子弹
                for (int j=0;j<et.ss.size();j++)
                {
                    //取出子弹
                    Shot es=et.ss.get(j);
                    if (es.is)
                    {
                        g.draw3DRect(es.x,es.y,1,1,false);
                    }else {
                        //如果敌人的坦克子弹死亡就从Vector中删掉
                        et.ss.remove(es);
                    }
                }
            }else {
                ets.remove(et);
            }
        }
    }
    //写一个函数判断子弹是否击中我的坦克
    /*
    public void hitmyTank(EnemyTank et)
    {
        //首先判断我的坦克的方向
        switch (hero.dir) {
            //如果我的坦克方向的上或者下
            case 0:
            case 2:
                for (int o = 0; o <  et.ss.size(); o++) {
                    if (et.ss.get(o).x > hero.x &&  et.ss.get(o).x < hero.x + 20 &&  et.ss.get(o).y > hero.y &&  et.ss.get(o).x < hero.y + 30) {
                        //击中
                        //子弹死亡
                         et.ss.get(o).is = false;
                        //我的坦克死亡
                        hero.islife = false;
                        //创建一颗炸弹放入Vector<Boom>
                        Boom b = new Boom(hero.x, hero.y);
                        booms.add(b);
                    }
                }
            case 1:
            case 3:
                for (int o = 0; o <  et.ss.size(); o++) {
                    if ( et.ss.get(o).x > hero.x &&  et.ss.get(o).x < hero.x + 30 &&  et.ss.get(o).y > hero.y &&  et.ss.get(o).x < hero.y + 20) {
                        //击中
                         et.ss.get(o).is = false;
                        //我的坦克死亡
                        hero.islife = false;
                        //创建一颗炸弹放入Vector<Boom>
                        Boom b = new Boom(hero.x, hero.y);
                        booms.add(b);
                    }
            }
        }
    }*/

    //判断敌人的子弹是否击中我
    public void hitMe()
    {
        for (int i=0;i<ets.size();i++) {
            for (int o = 0; o < ets.get(i).ss.size(); o++) {
                //取出子弹
                Shot es = ets.get(i).ss.get(o);

                if (es.is) {
                    //取出每个敌人坦克的子弹匹配，判断是否击中
                    if(hero.is) {
                        if(this.hitTank(es, hero));
                        {

                        }
                    }
                }
            }
        }
    }
    //写一个函数判断子弹是否击中敌人坦克
    public boolean hitTank(Shot s,Tank et)
    {
        boolean r=false;
        //首先判断该坦克的方向
        switch (et.dir)
        {
            //如果敌人坦克方向的上或者下
            case 0:
            case 2:
                if(et.x<s.x&&s.x<et.x+20&&s.y>et.y&&s.y<et.y+30)
                {
                    //击中
                    //子弹死亡
                    s.is=false;
                    //敌人坦克死亡
                    et.is=false;
                    r=true;
                    //创建一颗炸弹放入Vector<Boom>
                    Boom b=new Boom(et.x,et.y);
                    booms.add(b);
                }
                break;
            case 1:
            case 3:
                if (s.x>et.x&&s.x<et.x+30&&s.y>et.y&&s.y<et.y+20)
                {
                    //击中
                    //子弹死亡
                    s.is=false;
                    //敌人坦克死亡
                    et.is=false;
                    r=true;
                    //创建一颗炸弹放入Vector<Boom>
                    Boom b=new Boom(et.x,et.y);
                    booms.add(b);
                }
                break;
        }
        return r;

    }
    public void hitenTank()
    {
        //判断是否击中
        for (int i=0;i<hero.ss.size();i++)
        {
            //取出子弹
            Shot my=hero.ss.get(i);

            if (my.is)
            {
                //取出每个敌人坦克，与子弹匹配，判断是否击中
                for (int j=0;j<ets.size();j++)
                {
                    //取出坦克
                    EnemyTank et = ets.get(j);
                    if (et.is)
                    {
                        if(this.hitTank(my,et))
                        {
                            Recorder.reduceEnNum();
                            Recorder.addEnNumRec();
                        }
                    }
                }
            }
        }
    }
    public void drawTank(int x,int y,Graphics g,int direct,int type)
    {
        //判断坦克类型
        switch (type)
        {
            case 0:
                g.setColor(Color.red);
                break;
            case 1:
                g.setColor(Color.orange);
                break;
            case 2:
                g.setColor(Color.magenta);
        }
        //判断方向
        switch (direct)
        {
            case 0:
                //画出我的坦克
                //1.画出左边的矩形
                g.fill3DRect(x,y,5,30,false);
                //2.画出右边的矩形
                g.fill3DRect(x+15,y,5,30,false);
                //3.画出中间矩形
                g.fill3DRect(x+5,y+5,10,20,false);
                //4.画出炮管
                g.drawLine(x+10,y+10,x+10,y);
                //5.画出圆形
                g.fillOval(x+5,y+10,10,10);
                break;
            case 1:
                //向右
                g.fill3DRect(x,y,30,5,false);
                //2.画出右边的矩形
                g.fill3DRect(x,y+15,30,5,false);
                //3.画出中间矩形
                g.fill3DRect(x+5,y+5,20,10,false);
                //4.画出炮管
                g.drawLine(x+10,y+10,x+30,y+10);
                //5.画出圆形
                g.fillOval(x+10,y+5,10,10);
                break;
            case 2:
                //向右
                //1.画出左边的矩形
                g.fill3DRect(x,y,5,30,false);
                //2.画出右边的矩形
                g.fill3DRect(x+15,y,5,30,false);
                //3.画出中间矩形
                g.fill3DRect(x+5,y+5,10,20,false);
                //4.画出炮管
                g.drawLine(x+10,y+10,x+10,y+30);
                //5.画出圆形
                g.fillOval(x+5,y+10,10,10);
                break;
            case 3:
                //向右
                g.fill3DRect(x,y,30,5,false);
                //2.画出右边的矩形
                g.fill3DRect(x,y+15,30,5,false);
                //3.画出中间矩形
                g.fill3DRect(x+5,y+5,20,10,false);
                //4.画出炮管
                g.drawLine(x+10,y+10,x,y+10);
                //5.画出圆形
                g.fillOval(x+10,y+5,10,10);
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override//键按下处理
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()== KeyEvent.VK_W)
        {
            //设置我的坦克方向
            //向前
            this.hero.setDir(0);
            this.hero.moveUp();
        }else if (e.getKeyCode()== KeyEvent.VK_D)
        {
            //向右
            this.hero.setDir(1);
            this.hero.moveRight();
        }else if (e.getKeyCode()== KeyEvent.VK_S)
        {
            //向下
            this.hero.setDir(2);
            this.hero.moveDown();
        }else if (e.getKeyCode()==KeyEvent.VK_A)
        {
            //向左
            this.hero.setDir(3);
            this.hero.moveLeft();
        }
        if (e.getKeyCode()==KeyEvent.VK_J)
        {
            if (this.hero.goOn) {
                this.hero.shotE();
            }else {
                this.hero.xian+=3;
                if (this.hero.xian>=25)
                {
                    this.hero.setGoOn(true);
                }
            }

        }
        if (e.getKeyCode()==KeyEvent.VK_SPACE&&!this.stop)
        {
            for (EnemyTank et : this.ets) {
                et.val = 0;
                et.stop = true;
                for (int o = 0; o < et.ss.size(); o++) {
                    et.ss.get(o).val = 0;
                }
            }
            if (this.hero.ss.size()>0) {
                for (int l = 0; l < this.hero.ss.size(); l++) {
                    this.hero.ss.get(l).val = 0;
                }
            }
            this.hero.sval=0;
            this.stop=true;
        }else if (e.getKeyCode()==KeyEvent.VK_SPACE&&this.stop){
            for (EnemyTank et : this.ets) {
                et.val = 3;
                et.stop = false;
                for (int o = 0; o < et.ss.size(); o++) {
                    et.ss.get(o).val = 5;
                }
            }
            for (int l = 0; l<this.hero.ss.size(); l++)
            {
                this.hero.ss.get(l).val=5;
            }
            this.hero.sval=5;
            this.stop=false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (this.hero.ss.size()>0) {
            this.hero.goOn = true;
        }
    }

    @Override
    public void run() {
        while (true)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.hitenTank();
            this.hitMe();

            //重绘
            this.repaint();
        }
    }
}