package com.mega.service.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.mega.service.common.Constant;
import com.mega.service.data.IDataService;

/**
 * 多线程处理socket接收的数据
 * 
 * @author Andy
 *
 */
public class SocketOperate extends Thread {

	private Socket socket;

	public SocketOperate(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		// 根据输入输出流和客户端连接
		try {
			InputStream inputStream = socket.getInputStream();
			// 得到一个输入流，接收客户端传递的信息
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);// 提高效率，将自己字节流转为字符流
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);// 加入缓冲区
			String temp = null;
			String info = "";
			while ((temp = bufferedReader.readLine()) != null) {
				info += temp;
				System.out.println("已接收到客户端连接");
				System.out.println("服务端接收到客户端信息：" + info + ",当前客户端ip为：" + socket.getInetAddress().getHostAddress());
			}
			
			WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext(); 
			IDataService service = (IDataService) wac.getBean(IDataService.BEAN_ID);
			service.saveData(info);
			
			
			OutputStream outputStream = socket.getOutputStream();// 获取一个输出流，向客户端发送
			PrintWriter printWriter = new PrintWriter(outputStream);// 将输出流包装成打印流
			printWriter.print(Constant.SOCKET_RESULT_SUCCESS);
			printWriter.flush();
			socket.shutdownOutput();// 关闭输出流

			// 关闭相对应的资源
			bufferedReader.close();
			inputStream.close();
			printWriter.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}
}
