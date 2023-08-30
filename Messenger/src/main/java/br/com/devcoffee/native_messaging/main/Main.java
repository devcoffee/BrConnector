package br.com.devcoffee.native_messaging.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
        String outputPath = "C:/Balanca/log.txt";


		ObjectMapper mapper = new ObjectMapper();
		NativeRequest request = mapper.readValue(requestJson, NativeRequest.class);
        BufferedWriter out = new BufferedWriter(new FileWriter(outputPath));


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
		                out.write(line);

					} else {
						response.setMessage("Erro ao Coletar Peso");
					}
				} else {
					response.setMessage("Falha ao Conectar a Balanca (TCP Simples)");
				}

			} catch (IOException e) {
				response.setMessage("Erro ao Coletar Peso");
			}
		} else if (request.getContype().equals("TCPB")) {
			 try {
		            Socket socket = new Socket(request.getAddress(), request.getAddrport());
		            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		            char[] buffer = new char[1024];
		            int bytesRead;
		            while ((bytesRead = in.read(buffer)) != -1) {
		                String data = new String(buffer, 0, bytesRead);
		                
		                if (data != null) {
							response.setMessage(data);
							break;
						} else {
							response.setMessage("Erro ao Coletar Peso - TCPB");
						}
		            }

		            in.close();
		            socket.close();
		        } catch (IOException | InterruptedException e) {
					response.setMessage("Erro ao Coletar Peso - TCP2");
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
					} else {
						response.setMessage("Erro ao Coletar Peso - TCP2");
					}
					Thread.sleep(1000); // espera 1 segundo
				}

			} catch (IOException | InterruptedException e) {
				response.setMessage("Erro ao Coletar Peso - FILE" + e.getMessage());
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					response.setMessage("Erro ao Coletar Peso - FILE" + e.getMessage());
				}
			}
		} else if (request.getContype().equals("TCPL")) {
			if (System.getProperty("sun.arch.data.model").equals("64")) {
				response.setMessage(
						"Apenas Java 32 Bits Suportado para este tipo de conexao, consulte o suporte para mais informacoes");
			} else {
				try {
					String patch = "Indicador";
					Indicador ind = (Indicador) Native.loadLibrary(patch, Indicador.class);
					boolean status = ind.openTcp(request.getAddress(), request.getAddrport());
					if (status) {
						response.setMessage(ind.getPeso());
					} else {
						response.setMessage("Falha ao Conectar a Balanca (TCP Lider)");
					}
				} catch (Exception e) {
					response.setMessage(e.getLocalizedMessage());
				}
			}
		} else if (request.getContype().equals("SERIALL")) {
			if (System.getProperty("sun.arch.data.model").equals("64")) {
				response.setMessage(
						"Apenas Java 32 Bits Suportado para este tipo de conexao, consulte o suporte para mais informacoes");
			} else {
				try {
					String patch = "Indicador";
					Indicador ind = (Indicador) Native.loadLibrary(patch, Indicador.class);
					boolean status = ind.openSerial(request.getAddress(), request.getAddrport());
					if (status) {
						response.setMessage(ind.getPeso());
					} else {
						response.setMessage("Falha ao Conectar a Balanca (Serial Lider)");
					}
				} catch (Exception e) {
					response.setMessage(e.getLocalizedMessage());
				}
			}
		} else {
			response.setMessage("Metodo de Conexao nao suportado, consulte o suporte para mais informacoes");
		}

		// Send response message back
		String responseJson = mapper.writeValueAsString(response);
		sendMessage(responseJson);
		
        out.close();


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
