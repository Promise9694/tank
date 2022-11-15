package 坦克;
/**
 *  功能：坦克游戏的 2.01
 * 1 、画出我的坦克
 * 2 、让我的坦克动起来
 * 3 、让我的坦克按下空格 (space) 键发射子弹
 * 4 、让我的坦克可以连发子弹 ( 最多连发 5 颗子弹 )
 * 5 、打到敌人坦克，敌人坦克就消失
 * 6 、加入坦克被击中爆炸的效果
 * 7 、让敌人的坦克可以自由随机移动
 * 8 、控制坦克在指定的 MyPanel 面板中移动，不可越界
 * 9 、让敌人的坦克发射子弹
 * 10 、当我的坦克被敌人子弹击中，我的坦克爆炸
 * 11 、防止敌人坦克重叠运动
 * 12 、可以分关 -- 做一个开始的 Panel ，它是空的主要是提示关卡
 * 13 、游戏可以暂停、继续 -- 暂停时将子弹、坦克速度设为 0 ，坦克方向不变
 * 14 、可以记录玩家的成绩
 * 15 、 java 如何操作声音文件
 */
import java.io.*;
import java.util.Vector;

//恢复点
class Node
{
    int x;
    int y;
    int dir;
    public Node(int x,int y, int dir)
    {
        this.x=x;
        this.y=y;
        this.dir=dir;
    }
}

//记录类，同时也可以保存玩家的设置
class Recorder
{
    //记录美观有多少个敌人
    private static int enNum=15;
    //设置我的生命值
    private static int mylife=3;

    private static int AllEnNum=0;
    //从文件中恢复记录点
    static Vector<Node> nodes=new Vector<Node>();

    private static FileWriter fw=null;
    private static BufferedWriter bw=null;

    private static FileReader fr=null;
    private static BufferedReader br=null;

    private Vector<EnemyTank> ets=new Vector<EnemyTank>();

    //完成读取任务
    public void getNodes()
    {
        try {
            fr=new FileReader("d:/TankSave.txt");
            br=new BufferedReader(fr);
            String n="";
            //先读取第一行
            n=br.readLine();
            AllEnNum=Integer.parseInt(n);
            while ((n=br.readLine())!=null)
            {
                String[] xyz=n.split(" ");
                Node node=new Node(Integer.parseInt(xyz[0]),Integer.parseInt(xyz[1]),Integer.parseInt(xyz[2]));
                nodes.add(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //保存击毁敌人的数量和敌人坦克坐标，方向
    public void KeepEnmy()
    {
        {
            try {
                fw=new FileWriter("d:/TankSave.txt");
                bw=new BufferedWriter(fw);

                bw.write(AllEnNum+"\r\n");
                //保存当前活的敌人坦克坐标方向
                for (int i=0;i<ets.size();i++)
                {
                    //取出第一个坦克
                    EnemyTank et=ets.get(i);
                    if(et.is)
                    {
                        //活的保存
                        String re=et.x+" "+et.y+" "+et.dir;
                        bw.write(re+"\r\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    //后开先关闭
                    bw.close();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //从文件中读取，记录
    public static void getSave()
    {
        try {
            fr=new FileReader("d:/TankSave.txt");
            br=new BufferedReader(fr);
            String n=br.readLine();
            AllEnNum=Integer.parseInt(n);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //记录游戏信息
    public static void KeepInfo()
    {
        try {
            fw=new FileWriter("d:/TankSave.txt");
            bw=new BufferedWriter(fw);

            bw.write(AllEnNum+"\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                //后开先关闭
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Vector<EnemyTank> getEts() {
        return ets;
    }

    public void setEts(Vector<EnemyTank> ets) {
        this.ets = ets;
    }

    public static int getAllEnNum() {
        return AllEnNum;
    }

    public static void setAllEnNum(int allEnNum) {
        AllEnNum = allEnNum;
    }


    public static int getEnNum() {
        return enNum;
    }

    public static void setEnNum(int enNum) {
        Recorder.enNum = enNum;
    }

    public static int getMylife() {
        return mylife;
    }

    public static void setMylife(int mylife) {
        Recorder.mylife = mylife;
    }
    //减少敌人数
    public static void reduceEnNum()
    {
        enNum--;
    }
    public static void addEnNumRec(){
        AllEnNum++;
    }

}

//炸弹类
class Boom
{
    //定义炸弹的坐标
    int x,y;
    //炸弹的生命
    int life=21;
    boolean is=true;

    public Boom(int x,int y)
    {
        this.x=x;
        this.y=y;

    }

    //减少生命
    public void lifeDown()
    {
        if (life>0)
        {
            life--;
        }else {
            this.is=false;
        }
    }
}

//子弹类
class Shot implements Runnable
{
    int x;
    int y;
    int dir;
    int val;
    int xian=0;
    //是否还活着
    boolean is=true;
    public Shot(int x,int y,int dir,int val)
    {
        this.x=x;
        this.y=y;
        this.dir=dir;
        this.val=val;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            switch (dir) {
                case 0:
                    y -= val;
                    xian+=val;
                    break;
                case 1:
                    x += val;
                    xian+=val;
                    break;
                case 2:
                    y += val;
                    xian+=val;
                    break;
                case 3:
                    x -= val;
                    xian+=val;
                    break;
            }
            //判断该子弹是否碰到边缘
            if (x < 0 || x > 800 || y < 0 || y > 600) {
                    this.is=false;
                    break;
            }

        }
    }
}

//坦克类
class Tank
{
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
    //设定初始颜色
    int color;
    //坦克的速度
    int val=3;
    //表示坦克横坐标
    int x=0;
    //坦克的纵坐标
    int y=0;

    //坦克方向，0表示上，1表示右，2表示下，3表示左
    int dir=0;

    boolean is=true;
    boolean stop=false;

    public Tank(int x,int y)
    {
        this.x=x;
        this.y=y;
    }
}

//敌人的坦克
class EnemyTank extends Tank implements Runnable
{
    int times=0;
    //定义一个向量存放敌人子弹；
    Vector<Shot> ss=new Vector<Shot>();
    //敌人添加子弹，应当在刚刚创建坦克和敌人坦克子弹死亡后
    //定义一个向量可以访问到MyPanel上所有敌人的坦克
    Vector<EnemyTank> ets=new Vector<EnemyTank>();

    public EnemyTank(int x,int y)
    {
        super(x,y);
    }

    //得到MyPanel上的敌人坦克向量
    public void setEts(Vector<EnemyTank> ss)
    {
        this.ets=ss;
    }
    //判断是否碰撞
    public boolean isTouchOtherEnemy()
    {
        boolean b=false;
        switch (this.dir)
        {
            case 0:
                //向上
                //取出所有坦克
                for (int i=0;i<ets.size();i++)
                {
                    //取出第一个坦克
                    EnemyTank et=ets.get(i);
                    //如果不是自己
                    if (et!=this)
                    {
                        if (et.dir==0||et.dir==2)
                        {
                            if (this.x>=et.x&&this.x<=et.x+20&&this.y>=et.y&&this.y<=et.y+30)
                            {
                                return true;
                            }
                            if (this.x+20>=et.x&&this.x<=et.x+20&&this.y>=et.y&&this.y<=et.y+30)
                            {
                                return true;
                            }
                        }else if (et.dir==1||et.dir==3)
                        {
                            if (this.x>=et.x&&this.x<=et.x+30&&this.y>=et.y&&this.y<=et.y+20)
                            {
                                return true;
                            }
                            if (this.x+20>=et.x&&this.x<=et.x+30&&this.y>=et.y&&this.y<=et.y+20)
                            {
                                return true;
                            }
                        }
                    }
                }
                break;
            case 1:
                for (int i=0;i<ets.size();i++)
                {
                    //取出第一个坦克
                    EnemyTank et=ets.get(i);
                    //如果不是自己
                    if (et!=this)
                    {
                        if (et.dir==0||et.dir==2)
                        {
                            if (this.x+30>=et.x&&this.x+30<=et.x+20&&this.y>=et.y&&this.y<=et.y+30)
                            {
                                return true;
                            }
                            if (this.x+30>=et.x&&this.x+30<=et.x+20&&this.y+20>=et.y&&this.y+20<=et.y+30)
                            {
                                return true;
                            }
                        }else if (et.dir==1||et.dir==3)
                        {
                            if (this.x+30>=et.x&&this.x+30<=et.x+30&&this.y>=et.y&&this.y<=et.y+20)
                            {
                                return true;
                            }
                            if (this.x+30>=et.x&&this.x+30<=et.x+20&&this.y+20>=et.y&&this.y+20<=et.y+30)
                            {
                                return true;
                            }
                        }
                    }
                }
                break;
            case 2:
                for (int i=0;i<ets.size();i++)
                {
                    //取出第一个坦克
                    EnemyTank et=ets.get(i);
                    //如果不是自己
                    if (et!=this)
                    {
                        if (et.dir==0||et.dir==2)
                        {
                            if (this.x>=et.x&&this.x<=et.x+20&&this.y+30>=et.y&&this.y+30<=et.y+30)
                            {
                                return true;
                            }
                            if (this.x+20>=et.x&&this.x+20<=et.x+20&&this.y+30>=et.y&&this.y+30<=et.y+30)
                            {
                                return true;
                            }
                        }else if (et.dir==1||et.dir==3)
                        {
                            if (this.x>=et.x&&this.x<=et.x+30&&this.y+30>=et.y&&this.y+30<=et.y+20)
                            {
                                return true;
                            }
                            if (this.x+20>=et.x&&this.x+20<=et.x+30&&this.y+30>=et.y&&this.y+30<=et.y+20)
                            {
                                return true;
                            }
                        }
                    }
                }
                break;
            case 3:
                for (int i=0;i<ets.size();i++)
                {
                    //取出第一个坦克
                    EnemyTank et=ets.get(i);
                    //如果不是自己
                    if (et!=this)
                    {
                        if (et.dir==0||et.dir==2)
                        {
                            if (this.x>=et.x&&this.x<=et.x+20&&this.y>=et.y&&this.y<=et.y+30)
                            {
                                return true;
                            }
                            if (this.x>=et.x&&this.x<=et.x+20&&this.y+20>=et.y&&this.y+20<=et.y+30)
                            {
                                return true;
                            }
                        }else if (et.dir==1||et.dir==3)
                        {
                            if (this.x>=et.x&&this.x<=et.x+30&&this.y>=et.y&&this.y<=et.y+20)
                            {
                                return true;
                            }
                            if (this.x>=et.x&&this.x<=et.x+30&&this.y+20>=et.y&&this.y+20<=et.y+20)
                            {
                                return true;
                            }
                        }
                    }
                }
                break;
        }
        return b;
    }

    @Override
    public void run() {
        while (true) {
            switch (this.dir) {
                case 0:
                    //说明坦克正在向上移动
                    for (int i=0;i<10;i++) {
                        if (y>0&&!this.isTouchOtherEnemy()) {
                            y -= val;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    for (int i=0;i<30;i++) {
                        if (x<750&&!this.isTouchOtherEnemy()) {
                            x += val;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 2:
                    for (int i=0;i<30;i++) {
                        if (y<530&&!this.isTouchOtherEnemy()) {
                            y += val;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 3:
                    for (int i=0;i<30;i++) {
                        if (x>0&&!this.isTouchOtherEnemy()) {
                            x -= val;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
            this.times++;

            if (times%2==0&&!this.stop){
                if (is)
                {
                    //判断子弹是否没有
                    if (ss.size()<5)
                    {
                        //没有子弹
                        //添加
                        Shot s=null;
                        switch (dir) {
                            case 0:
                                //创建一颗子弹
                                s = new Shot( x + 10,  y, 0,5);
                                //把子弹加入到向量
                                 ss.add(s);
                                break;
                            case 1:
                                s = new Shot( x + 30,  y + 10, 1,5);
                                 ss.add(s);
                                break;
                            case 2:
                                s = new Shot( x + 10,  y + 30, 2,5);
                                 ss.add(s);
                                break;
                            case 3:
                                s = new Shot( x,  y + 10, 3,8);
                                 ss.add(s);
                                break;
                        }
                        //启动子弹
                        Thread t=new Thread(s);
                        t.start();
                    }
                }
            }

            //让坦克随即产生一个新的方向
            if (!this.stop) {
                this.dir = (int) (Math.random() * 4);
            }
            //判断敌人是否死亡
            if (!this.is)
            {
                //让坦克死亡后退出线程
                break;
            }
        }
    }
}

//玩家的坦克
class Hero extends Tank implements Runnable{
    //子弹
    int xian=0;
    int val=3;

    boolean freez=false;
    int sval;
    public void setGoOn(boolean goOn) {
        this.goOn = goOn;
    }

    public boolean isGoOn() {
        return goOn;
    }

    boolean goOn=true;
    Vector<Shot> ss=new Vector<Shot>();
    Shot s=null;
    public Hero(int x, int y,int sval) {
        super(x, y);
        this.sval=sval;
    }


    //开火
    public void shotE()
    {
        switch (this.dir) {
            case 0:
                //创建一颗子弹
                s = new Shot(x + 10, y, 0,sval);
                xian=0;
                goOn=false;
                //把子弹加入到向量
                ss.add(s);
                break;
            case 1:
                s = new Shot(x + 30, y + 10, 1,sval);
                xian=0;
                goOn=false;
                ss.add(s);
                break;
            case 2:
                s = new Shot(x + 10, y + 30, 2,sval);
                xian=0;
                goOn=false;
                ss.add(s);
                break;
            case 3:
                s = new Shot(x, y + 10, 3,sval);
                xian=0;
                goOn=false;
                ss.add(s);
                break;
        }
        //
        Thread t=new Thread(s);
        t.start();

    }
    //坦克向上移动
    public void moveUp() {
        y -= val;
    }

    //坦克向右移动
    public void moveRight() {
        x += val;
    }

    //坦克向下移动
    public void moveDown() {
        y += val;
    }

    //坦克向左移动
    public void moveLeft() {
        x -= val;
    }

    @Override
    public void run() {

    }
}