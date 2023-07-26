package quiz;

import java.io.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;
import java.lang.Thread;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import java.sql.*;


class Mail
{
	public static void push_email(String recepient , int scr , int endTime , String leaderboard)
	{
		System.out.println();
		System.out.println("Composing e-mail...");
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		
		//We must provide the email and password of host
		String username = "";//Enter host email
		String password = "";//Enter host email's password
		
		Session session = Session.getInstance(properties,new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username , password);
			
			}
		});
		
		Message content = prepareMessage(session,username,recepient,scr,endTime,leaderboard);
		try
		{
			Transport.send(content);
		}
		catch(Exception me)
		{
			me.printStackTrace();
		}
		System.out.println("E-mail delivered successfully to "+recepient);
	}
	
	private static Message prepareMessage(Session session,String username,String recepient,int mrk,int endTime,String leaderboard)
	{
		Message message = new MimeMessage(session);
		String body_of_email="";
		body_of_email = "Hi there, \nThanks for attending the quiz\n\nYou have scored "
						+mrk+" points with an on quiz time of "+endTime+"s"+"\n\n\t\tLEADERBOARD\nRANK\tSCORE\t   TIME\t       USERNAME\n"
						+leaderboard+"\nKeep Quizzing!";
		
		try
		{
			message.setFrom(new InternetAddress(username));
			message.setRecipient(Message.RecipientType.TO,new InternetAddress(recepient));
			message.setSubject("Quiz : Graded");
			message.setText(body_of_email);
		}
		catch(Exception j)
		{
			j.printStackTrace();
		}
		return message;
	}
}


 class ques_generator
{     ArrayList<String> mcq_Ques;
      ArrayList<String> mcq_Soln;
    
    public void updatefile()
    {
        try
        {  
            mcq_Ques = new ArrayList<String>();
            mcq_Soln = new ArrayList<String>();
            String filePath = new File("").getAbsolutePath();
            File file=new File(filePath+"/questions.txt");  
            FileReader fr=new FileReader(file);    
            BufferedReader br=new BufferedReader(fr); 
            String line;
            int turn = 1;
            while( (line=br.readLine()) != null )
            {
                if(turn == 1)
                {
                    mcq_Ques.add(line);
                    turn += 1;
                }
                else if(turn == 2)
                {
                    
                    mcq_Ques.add(line);
                    turn += 1;
                }
                else
                {
                    mcq_Soln.add(line);
                    turn += 1;
                    turn = turn % 3;
                }
            }
            System.out.println("Questions updated");
            System.out.println();
        }
        catch(Exception e)
        {
            System.out.println("Exception in file handling");
        }
        
    }
}





class limit_client
{
    public static int no_of_clients=0;
}



public class Quiz_Server  
{
   static Date dt;
   public static void main(String args[]) throws Exception
    {
    try (ServerSocket soc = new ServerSocket(8888)) {
        dt = new Date();
        System.out.println("Server at port 8888 started at "+dt.toString());
        while( (limit_client.no_of_clients) < 2)
        {
            System.out.println("Waiting for connection..");
            OperateClient oc = new OperateClient(soc.accept());
        }
    }
    catch(Exception e)
    {
    	
    }
}
}

class OperateClient extends Thread 
{
    Socket Recv_Socket;
    ObjectInputStream input;
    ObjectOutputStream output;
    int totalClients = 50;
    int port = 8888;
    String login_msg = "";
    String signup_msg="";
    String ans="";
    byte option;
    boolean flag=false;
    ArrayList<String> email = new ArrayList<String>();
    ArrayList<String> pwd = new ArrayList<String>();
    ArrayList<String> qlist = new ArrayList<String>(); 
    ArrayList<String> soln = new ArrayList<String>();
    Integer score = 0;
    Integer ind = 0;
    Integer finish_time=0;
    Date d;
    String rcpt_email="";
    String[] login_components;
    String leaderboard_msg="";
    ques_generator qg = new ques_generator();
    
    
    OperateClient(Socket soc)
    {
      limit_client.no_of_clients += 1;
      
        try
        {
            Recv_Socket = soc;
            d = new Date();
            System.out.println(Recv_Socket.getInetAddress().getHostName()+" connected on "+d.toString());
            start();

        }
        catch(Exception ex)
        {
        	
        }
    }
    
    public void send_ques()
      {				qg.updatefile();
                    try
                    {                        
                        output.writeObject(qg.mcq_Ques);
                        output.flush();
                        
                    }
                    catch(Exception exc)
                    {
                        System.out.println("Unable to send questions !");
                    }
      }
      
    public boolean evaluate(String[] str)
    {   
    	try
    	{
    		//Class.forName("com.mysql.jdbc.Driver");
    		Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizdb","root","");
    		Statement stmt = c.createStatement();
    		String sql = "Select * from players where Email='"+str[0]+"' and Password='"+str[1]+"'";
    		ResultSet rs = stmt.executeQuery(sql);
    		if(rs.next())
    		{
    			return true;
    		}
    		
    	}
    	catch(Exception e)
    	{
    		
    	}
    	return false;
    }
    
    public void credential_check(String msg)
    {
        String str="Wrong_login";
        flag=false;
        login_components = msg.split(" ");
        rcpt_email = login_components[0];
        if( evaluate(login_components) )
        {
            str="Proceed_login";
            flag=true;
            System.out.println(login_components[0]+" logged in successfully..");
        }
        
        			try
                    {                        
                        output.writeObject(str);
                        output.flush();
                        
                    }
                    catch(Exception exc)
                    {
                        System.out.println("Unable to connect !");
                    }
       
    }

    public String login_ver()
    {
        try
        {
            login_msg = (String) input.readObject();
        }
        catch(Exception e)
        {
        	
        }

        return login_msg;
    }

    public void score_update()
    {
        try
        {
            ans = (String) input.readObject();
            System.out.println("Recieved answer from participant : "+ans);
        }
        catch(Exception e)
        {
        	System.out.println("Exception in recieving answer in server side");
        }
        if(ind < qg.mcq_Soln.size())
        {
            if(qg.mcq_Soln.get(ind).equals(ans))
            {
                score = score + 1;
                System.out.println("Correct Answer");
            }
            else
            {
            	System.out.println("Wrong Answer");
            }
            ind = ind + 1;
        }
    }
    
    public void send_score()
    {
        try
        {
        output.writeObject(Integer.toString(score));
        System.out.println("Sent score as : "+score);
        output.flush();
        }
        catch(Exception z)
        {
            System.out.println("Unable to send scores from server !");
        }
    }
    
    public void create_Account()
    {
		try
	    {
	        signup_msg = (String) input.readObject();
	        System.out.println(login_msg);
	    }
	    catch(Exception e)
	    {
	    	
	    }
	
		String[] signup_components;
		signup_components = signup_msg.split(" ");
		
		try
		{
			//Class.forName("com.mysql.jdbc.Driver");
			Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizdb","root","");
			Statement stmt = c.createStatement();
			String q = "INSERT INTO players(Username,Email,Password) VALUES('"+signup_components[0]+"','"+signup_components[1]+"','"+signup_components[2]+"')";
			stmt.executeUpdate(q);
		}
		catch(Exception e)
		{
			
		}
		
    }

    public void recv_total_time()
    {
		try
	    {
	        finish_time = (Integer) input.readObject();
	        System.out.println("Quiz completed after : "+finish_time+"s");
	    }
	    catch(Exception e)
	    {
	    	
	    }
    }

    public void update_retrieve_db()
    {
		int rank = 1;
		try
		{
			//Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Score:"+score+" & "+"Finish time:"+finish_time);
			Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizdb","root","");
			Statement stmt = c.createStatement();
			String q = "UPDATE players SET Score='"+Integer.toString(score)+"',FinishTime='"+Integer.toString(finish_time)+"' WHERE Email='"+login_components[0]+"'";
			stmt.executeUpdate(q);
		}
		catch(Exception e)
		{
			
		}
	
		try
		{
			//Class.forName("com.mysql.jdbc.Driver");
			
			Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizdb","root","");
			Statement stmt = c.createStatement();
			String rtr = "SELECT * FROM players ORDER BY Score DESC, FinishTime";
			ResultSet rs = stmt.executeQuery(rtr);
			
			while(rs.next())
			{
				String s1 = rs.getString("Username");
				Integer n1 = rs.getInt("Score");
				Integer n2 = rs.getInt("FinishTime");
				
				leaderboard_msg += Integer.toString(rank)+"\t\t "+n1+"\t\t"+n2+"s"+"\t\t"+s1+"\n";
				rank++;
				
				
			}
			
		}
		catch(Exception e)
		{
			
		}
		
    }


    public void run() 
    {
    
	    boolean loop_check = true;
	    try
	    {
        String file_path = new File("").getAbsolutePath();
        FileInputStream fis = new FileInputStream(file_path+"/Quiz-Guidelines.txt");
		byte[] byte_str = new byte[40000];
		fis.read(byte_str,0,byte_str.length);
		OutputStream ostrm = Recv_Socket.getOutputStream();
		ostrm.write(byte_str,0,byte_str.length);
        input = new ObjectInputStream(Recv_Socket.getInputStream());
        output = new ObjectOutputStream(Recv_Socket.getOutputStream());
        output.flush();
	    }
	    catch(Exception mrt)
	    {
	    	
	    }
		while(loop_check)
	                {
	                    try
	                    {
	                        option = input.readByte();
	                        switch(option)
	                        {
	                            case 1: credential_check(login_ver());
	                                    if(flag==true)
	                                    {
	                                        send_ques();
	                                    }
	                                    break;
	                            case 2: score_update();
	                                    break;
	                            case 3:send_score();
	                                    break;
	                            case 4: create_Account();
	                            		break;
	                            case 5: recv_total_time();
	                    				break;
	                            case 6: update_retrieve_db();
	            						break;
	                            default:limit_client.no_of_clients -= 1;
	                            		
	                                     break;
	                        }
	                    }
	                    catch(Exception ioe)
	                    {
	                        System.out.println(rcpt_email+" completed the quiz");
	                        Mail.push_email(rcpt_email,score,finish_time,leaderboard_msg);
	                        loop_check = false;
	                        try
	                        {
	                            Recv_Socket.close();
	                        }
	                        catch(Exception e)
	                        {
	
	                        }
	                    }
	                }
    
    	}
}
