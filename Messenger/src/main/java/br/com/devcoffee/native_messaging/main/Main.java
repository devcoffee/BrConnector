package br.com.devcoffee.native_messaging.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.Native;

import br.com.devcoffee.native_messaging.lider.Indicador;
import br.com.devcoffee.native_messaging.protocol.NativeRequest;
import br.com.devcoffee.native_messaging.protocol.NativeResponse;

public class Main {

	public static void main(String[] args) throws Exception {

		// Read message
		String requestJson = readMessage(System.in);

		ObjectMapper mapper = new ObjectMapper();
		NativeRequest request = mapper.readValue(requestJson, NativeRequest.class);

		// Process request...
		NativeResponse response = new NativeResponse();

		if (request.getContype().equals("TCP")) {
			try {
				Socket clientSocket = new Socket(request.getAddress(), request.getAddrport());
				if (clientSocket.isConnected()) {
					InputStream is = clientSocket.getInputStream();

					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String line = reader.readLine();
					clientSocket.close();
					if (line != null) {
						response.setMessage(line);
					} else {
						response.setMessage("Erro ao realizar leitura (TCP)");
					}
				} else {
					response.setMessage("Falha ao Conectar (TCP)");
				}

			} catch (Exception e) {
				response.setMessage("Erro (TCP) - " + e.getMessage());
			}
		} else if (request.getContype().equals("TCPB")) {
			
			try {
				Socket socket = new Socket(request.getAddress(), request.getAddrport());
				InputStream in = socket.getInputStream();

	            int byteRead;
	            StringBuilder strRet = new StringBuilder();
	            while (true) {
	                byteRead = in.read();
	                if (byteRead == -1) {
	                    break;
	                }
	                
	                if (byteRead == 32) {
	                	strRet.setLength(0);
	                }

	                strRet.append((char) byteRead);
	                if (strRet.length() % 26 == 0) {
	                	response.setMessage(strRet.toString());
	                    strRet.setLength(0);
	                }
	            }
	        } catch (Exception e) {
				response.setMessage("Erro (TCPB) - " + e.getMessage());
	        } 

		} else if (request.getContype().equals("FILE")) {
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(request.getAddress()));
				String line;

				while ((line = reader.readLine()) != null) {
					if (line != null) {
						response.setMessage(line);
						break;
					}
				}

			} catch (Exception e) {
				response.setMessage("Erro (FILE) - " + e.getMessage());
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (Exception e) {
					response.setMessage("Erro (FILE) - " + e.getMessage());
				}
			}
		} else if (request.getContype().equals("TCPL")) {
			if (System.getProperty("sun.arch.data.model").equals("64")) {
				response.setMessage(
						"Apenas Java 32 Bits Suportado para este tipo de conexao (TCPL), consulte o suporte para mais informacoes");
			} else {
				try {
					String patch = "Indicador";
					Indicador ind = (Indicador) Native.loadLibrary(patch, Indicador.class);
					boolean status = ind.openTcp(request.getAddress(), request.getAddrport());
					if (status) {
						response.setMessage(ind.getPeso());
					} else {
						response.setMessage("Falha ao Conectar (TCPL)");
					}
				} catch (Exception e) {
					response.setMessage("Erro (TCPL) - " + e.getMessage());
				}
			}
		} else if (request.getContype().equals("SERIALL")) {
			if (System.getProperty("sun.arch.data.model").equals("64")) {
				response.setMessage(
						"Apenas Java 32 Bits Suportado para este tipo de conexao (SERIALL), consulte o suporte para mais informacoes");
			} else {
				try {
					String patch = "Indicador";
					Indicador ind = (Indicador) Native.loadLibrary(patch, Indicador.class);
					boolean status = ind.openSerial(request.getAddress(), request.getAddrport());
					if (status) {
						response.setMessage(ind.getPeso());
					} else {
						response.setMessage("Falha ao Conectar (SERIALL)");
					}
				} catch (Exception e) {
					response.setMessage("Erro (TCPL) - " + e.getMessage());
				}
			}
		} else {
			response.setMessage("Metodo de Conexao nao suportado (" + request.getContype()
					+ ") , consulte o suporte para mais informacoes");
		}

		// Send response message back
		String responseJson = mapper.writeValueAsString(response);
		sendMessage(responseJson);

		System.exit(0);
	}

	private static String readMessage(InputStream in) throws IOException {
		byte[] b = new byte[4];
		in.read(b); // Read the size of message

		int size = getInt(b);

		if (size == 0) {
			throw new InterruptedIOException("Blocked communication");
		}

		b = new byte[size];
		in.read(b);

		return new String(b, "UTF-8");
	}

	private static void sendMessage(String message) throws IOException {
		System.out.write(getBytes(message.length()));
		System.out.write(message.getBytes("UTF-8"));
		System.out.flush();
	}

	public static int getInt(byte[] bytes) {
		return (bytes[3] << 24) & 0xff000000 | (bytes[2] << 16) & 0x00ff0000 | (bytes[1] << 8) & 0x0000ff00
				| (bytes[0] << 0) & 0x000000ff;
	}

	public static byte[] getBytes(int length) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (length & 0xFF);
		bytes[1] = (byte) ((length >> 8) & 0xFF);
		bytes[2] = (byte) ((length >> 16) & 0xFF);
		bytes[3] = (byte) ((length >> 24) & 0xFF);
		return bytes;
	}
}
