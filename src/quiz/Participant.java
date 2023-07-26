package quiz;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.Timer;
import java.text.DecimalFormat;

public class Participant extends JFrame{	

    Image img_login,img_signup,img_quiz;
	
	
	ObjectOutputStream output;
    ObjectInputStream input;
    String message="";
    String serverIP;
    Socket connection;				
    int port = 8888;
    String checkstr="";
    
    //Components in login page
    JFrame f,fmain,fsignup;
    JLabel login;
    JLabel email,pwd,time_label,bg_label,info;
    JTextField tf_email;
    JButton login_send,signup,next;
    JPasswordField tf_pwd;
    
    //Components in signup page
    JTextField sutf_uname,sutf_email;
    JPasswordField supf_pwd;
    JLabel su_uname,su_email,su_pwd,subg_label,su_title;
    JButton create_button;
    
    //Components in main window
    JRadioButton r1,r2,r3,r4;
    JLabel ques,counter_label;
    Timer tmr,tot_timer;
    ButtonGroup bg;
    
    ArrayList<String> qdisp = new ArrayList<String>();
    Integer indx=0;
    String[] res;
    String get_q="";
    
    //Components for formatting
    int sec,min,tot_sec;
    String ddSecond,ddMinute;
    DecimalFormat dFormat;
    Font font1;
    boolean cease_check = false;
    
    public static final Pattern authorised_email = 
    	    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    	public static boolean validate(String emailStr) {
    	        Matcher identical = authorised_email.matcher(emailStr);
    	        return identical.find();
    	}
    
    
    public Participant(String s) 
    {
		login_gui();
        serverIP = s;
    }
    
    public void signup_gui()
    {
    	f.setVisible(false);
    	
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 573, 387);
		JPanel canvas = new JPanel();
		canvas.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(canvas);
		canvas.setLayout(null);
		
		JLabel su_title = new JLabel("SIGN-UP");
		su_title.setForeground(new Color(255, 255, 255));
		su_title.setHorizontalAlignment(SwingConstants.CENTER);
		su_title.setBounds(227, 11, 79, 54);
		canvas.add(su_title);
		
		JLabel su_uname = new JLabel("Username");
		su_uname.setForeground(new Color(255, 255, 255));
		su_uname.setHorizontalAlignment(SwingConstants.CENTER);
		su_uname.setBounds(117, 82, 89, 14);
		canvas.add(su_uname);
		
		JLabel su_email = new JLabel("E-Mail");
		su_email.setForeground(new Color(255, 255, 255));
		su_email.setHorizontalAlignment(SwingConstants.CENTER);
		su_email.setBounds(117, 125, 89, 14);
		canvas.add(su_email);
		
		JLabel su_pwd = new JLabel("Password");
		su_pwd.setForeground(new Color(255, 255, 255));
		su_pwd.setHorizontalAlignment(SwingConstants.CENTER);
		su_pwd.setBounds(117, 166, 89, 14);
		canvas.add(su_pwd);
		
		sutf_uname = new JTextField();
		sutf_uname.setBounds(237, 79, 171, 20);
		canvas.add(sutf_uname);
		sutf_uname.setColumns(10);
		
		sutf_email = new JTextField();
		sutf_email.setBounds(237, 122, 171, 20);
		canvas.add(sutf_email);
		sutf_email.setColumns(10);
		
		supf_pwd = new JPasswordField();
		supf_pwd.setBounds(237, 163, 171, 20);
		canvas.add(supf_pwd);
		
		JButton create_button = new JButton("CREATE");
		create_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				
				
				if(validate(sutf_email.getText()))
				{
					try
	                {
	                output.writeByte(4);
					output.writeObject(sutf_uname.getText()+" "+sutf_email.getText()+" "+new String(supf_pwd.getPassword()));
					output.flush();
	                }
	                catch(Exception exc)
	                {
	                    System.out.println(exc);
	                }
					
					JOptionPane.showMessageDialog(f,"Account Created");
					f.setVisible(true);
					dispose();
				}
				else
				{
					JOptionPane.showMessageDialog(f,"Enter valid E-mail");
					sutf_uname.setText("");
					sutf_email.setText("");
					supf_pwd.setText("");
				}
				
				
			}
		});
		create_button.setBounds(217, 229, 89, 23);
		canvas.add(create_button);
		
		JLabel subg_label = new JLabel("");
		try {
			img_signup = ImageIO.read(new FileInputStream("Background_images/bg_signup.jpg"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		subg_label.setIcon(new ImageIcon(img_signup));
		subg_label.setBounds(0, 0, 559, 350);
		canvas.add(subg_label);
		
		setVisible(true);
    }
    
    public void login_gui()
    {
    	
		f = new JFrame();
		f.setTitle("QuizUp");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(100, 100, 572, 383);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		login = new JLabel("LOGIN");
		login.setForeground(new Color(255, 255, 255));
		login.setHorizontalAlignment(SwingConstants.CENTER);
		login.setBounds(225, 29, 69, 40);
		contentPane.add(login);
		
		email = new JLabel("E-Mail");
		email.setForeground(new Color(255, 255, 255));
		email.setHorizontalAlignment(SwingConstants.CENTER);
		email.setBounds(121, 94, 89, 28);
		contentPane.add(email);
		
		pwd = new JLabel("Password");
		pwd.setForeground(new Color(255, 255, 255));
		pwd.setHorizontalAlignment(SwingConstants.CENTER);
		pwd.setBounds(131, 133, 71, 28);
		contentPane.add(pwd);
		
		tf_email = new JTextField();
		tf_email.setBounds(238, 98, 173, 20);
		contentPane.add(tf_email);
		tf_email.setColumns(10);
		
		tf_pwd = new JPasswordField();
		tf_pwd.setBounds(238, 137, 173, 20);
		contentPane.add(tf_pwd);
		
		login_send = new JButton("LOGIN");
		login_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try
	                {
	                output.writeByte(1);
					output.writeObject(tf_email.getText()+" "+new String(tf_pwd.getPassword()));
					output.flush();
	                }
	                catch(Exception exc)
	                {
	                    System.out.println(exc);
	                }

	                is_valid();
				
			
			}
		});
		login_send.setBounds(217, 203, 89, 23);
		contentPane.add(login_send);
		
		signup = new JButton("SIGNUP");
		signup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				signup_gui();
				
			}
		});
		signup.setBounds(217, 296, 89, 23);
		contentPane.add(signup);
		
		info = new JLabel("Don't have an Account?");
		info.setForeground(new Color(255, 255, 255));
		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setBounds(166, 257, 199, 28);
		contentPane.add(info);
		
		bg_label = new JLabel("");
		try {
			img_login = ImageIO.read(new FileInputStream("Background_images/bg_login.jpg"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		bg_label.setIcon(new ImageIcon(img_login));
		bg_label.setBounds(0, 0, 558, 346);
		contentPane.add(bg_label);
		
		f.add(contentPane);		
		f.setVisible(true);
		
		
    }
    
    
   public void Question_timekeeper()
   {
	   tmr = new Timer(1000,new ActionListener() {
		   @Override
		   public void actionPerformed(ActionEvent aet)
		   {
			   sec--;
			   ddSecond = dFormat.format(sec);
			   ddMinute = dFormat.format(min);
			   counter_label.setText(ddMinute+":"+ddSecond);
			   
			   if(min == 0 && sec == 0)
			   {
				   next.doClick();
				   sec = 15;
				   min = 0;
				   counter_label.setText("00:15");
				   tmr.restart();
			   }
			   
			   if(sec == -1)
			   {
				   sec = 59;
				   min--;
				   ddSecond = dFormat.format(sec);
				   ddMinute = dFormat.format(min);
				   counter_label.setText(ddMinute+":"+ddSecond);
			   }
		   }
	   });
   }
    
   public void Overall_timekeeper()
   {
	   tot_timer = new Timer(1000,new ActionListener() {
		   @Override
		   public void actionPerformed(ActionEvent aett)
		   {
			   tot_sec++;
		   }
	   });
   }
   
    public void quiz_confirm()
    {
        String conf="";
        try
        {
            conf = (String)input.readObject();
            JOptionPane.showMessageDialog(f,"Score : "+conf+"\nCheck you E-Mail for more details!");
        }
        catch(Exception g)
        {
            System.out.println("Unable to recieve scores !");
        }
        
        try
        {
        output.writeByte(5);
        output.writeObject(tot_sec);
        output.flush();
        }
        catch(Exception ex)
        {
           ex.printStackTrace();
        }
    }
    
    
    public void end_quiz()
    {
    	 try
         {
         output.writeByte(6);
         output.flush();
         }
         catch(Exception ex)
         {
            ex.printStackTrace();
         }
    	 System.out.println();
    	 System.out.println("Thank You | Quiz over");
    	 System.out.println("Check your E-Mail for results");
    }
    
    

    private void Main_gui()
    {
        
    	login_send.setEnabled(false);       
        f.setVisible(false);
        
        tot_sec = 0;
        Overall_timekeeper();
        tot_timer.start();
        
        font1 = new Font("Arial",Font.PLAIN,20);
        dFormat = new DecimalFormat("00");
        sec = 15;
        min = 0;
        Question_timekeeper();
        tmr.start();
        
        get_q = qdisp.get(indx);
        indx += 1;
        res = qdisp.get(indx).split(" ");
    	
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 545, 380);
		JPanel contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		ques = new JLabel(get_q);
		ques.setForeground(new Color(255, 255, 255));
		ques.setHorizontalAlignment(SwingConstants.CENTER);
		ques.setBounds(71, 27, 352, 42);
		contentPane.add(ques);
		
		r1 = new JRadioButton(res[0]);
		r1.setBackground(new Color(9, 13, 22));
		r1.setForeground(new Color(255, 255, 255));
		r1.setBounds(78, 92, 176, 23);
		contentPane.add(r1);
		
		r2 = new JRadioButton(res[1]);
		r2.setForeground(new Color(255, 255, 255));
		r2.setBackground(new Color(9, 13, 22));
		r2.setBounds(78, 142, 176, 23);
		contentPane.add(r2);
		
		r3 = new JRadioButton(res[2]);
		r3.setBackground(new Color(9, 13, 22));
		r3.setForeground(new Color(255, 255, 255));
		r3.setBounds(287, 92, 176, 23);
		contentPane.add(r3);
		
		r4 = new JRadioButton(res[3]);
		r4.setForeground(new Color(255, 255, 255));
		r4.setBackground(new Color(9, 13, 22));
		r4.setBounds(287, 142, 176, 23);
		contentPane.add(r4);
		
		bg = new ButtonGroup();
		bg.add(r1);
		bg.add(r2);
		bg.add(r3);
		bg.add(r4);
		
		next = new JButton("NEXT");
		
		
		
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				
				String response="";
                if(r1.isSelected())
                {
                    response = r1.getText();
                }
                else if(r2.isSelected())
                {
                    response = r2.getText();
                }
                else if(r3.isSelected())
                {
                    response = r3.getText();
                }
                else if(r4.isSelected())
                {
                    response = r4.getText();
                }
                else
                {
                	response = "";
                }
                
                try
                {
                output.writeByte(2);
				output.writeObject(response);
				output.flush();
                }
                catch(Exception exc)
                {
                    System.out.println("Unable to connect !");
                }
                
                if(indx < qdisp.size()-1)
                {
                indx = indx + 1;
                sec=15;
                min=0;
                counter_label.setText("00:15");
                tmr.restart();
                }
                else
                {
                    tmr.stop();
                    tot_timer.stop();
                    cease_check = true;
                	next.setEnabled(false); 
                    try
                    {
                    output.writeByte(3);
                    output.flush();
                    }
                    catch(Exception f)
                    {
                       f.printStackTrace();
                    }
                    quiz_confirm();
                    end_quiz();
                }
                if(cease_check == false)
                {
                	 bg.clearSelection();
                     get_q = qdisp.get(indx);
                     indx += 1;
                     res = qdisp.get(indx).split(" ");
                     ques.setText(get_q);
                     r1.setText(res[0]);
                     r2.setText(res[1]);
                     r3.setText(res[2]);
                     r4.setText(res[3]);
                }
                
                
               
			}
		});
		next.setBounds(201, 217, 89, 23);
		contentPane.add(next);
		
		
		
		counter_label = new JLabel("00:15");
		counter_label.setForeground(new Color(255, 255, 255));
		counter_label.setHorizontalAlignment(SwingConstants.CENTER);
		counter_label.setBounds(433, 22, 98, 52);
		contentPane.add(counter_label);
		
		
		JLabel main_bglabel = new JLabel("");
		try {
			img_quiz = ImageIO.read(new FileInputStream("Background_images/bg_quiz.jpg"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		main_bglabel.setIcon(new ImageIcon(img_quiz));
		main_bglabel.setBounds(0, 0, 531, 343);
		contentPane.add(main_bglabel);
		
		setVisible(true);
    	
    }

    public void is_valid()
    {
       
         try
         {
           checkstr = (String) input.readObject();
           if(checkstr.equals("Proceed_login"))
           {
        	   System.out.println("Login successful");
           }
           else
           {
        	   System.out.println("Enter valid login credentials");
           }
         }
         catch(Exception exc)
         {
           System.out.println("Unable to connect !");
         }
       
        
        
        
        if(checkstr.equals("Proceed_login"))
        {           
         try
         {
          Object obj = input.readObject();
          qdisp =  (ArrayList<String>) obj;
          System.out.println("Recieved Questions from server");
         }
         catch(Exception exc)
         {
           System.out.println("Unable to read question list in client side !");
         }
            
            
         Main_gui();
        }
        else
        {
             tf_email.setText("");
             tf_pwd.setText("");
             JOptionPane.showMessageDialog(f,"Invalid Username or Password");  
        }
        
        
        
    }
    
   
    public void Execution()
    {
       try
       {
            System.out.println("Attempting Connection ...");
            try
            {
                connection = new Socket(InetAddress.getByName(serverIP),port);
                byte []byte_buffer = new byte[40000];
        		InputStream istrm = connection.getInputStream();
        		FileOutputStream fos = new FileOutputStream("D:\\Quiz-Guidelines.txt");
        		istrm.read(byte_buffer,0,byte_buffer.length);
        		fos.write(byte_buffer,0,byte_buffer.length);
                System.out.println("Connected to: " + connection.getInetAddress().getHostName());
            }catch(IOException ioException)
            {
                    JOptionPane.showMessageDialog(null,"Server Might Be Down!","Warning",JOptionPane.WARNING_MESSAGE);
            }
            
            

            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());

            
       }
       catch(IOException ioException)
       {
            ioException.printStackTrace();
       }
    }
    public static void main(String[] args) 
	{
		Participant sl=new Participant("localhost");
        sl.Execution();
        
	}
}

