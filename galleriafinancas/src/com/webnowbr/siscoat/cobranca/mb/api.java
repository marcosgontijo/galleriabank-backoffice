import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.http.util.EntityUtils;

import com.webnowbr.siscoat.cobranca.db.model.APIClient;
import com.webnowbr.siscoat.cobranca.db.model.FileOutPutStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class api {
    public static void main(String [] args) {
        try {
            //URL da API
            String apiUrl = "https://api.prd.valuation.eemovel.com.br/valuation/assessment/internal/realty/calculator";

            //Criação API
            URL url = new URL(apiUrl);

            //Criação da conexão
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("x-api-key", "TAwbnRfQeH4Yg6mB6YZsk42tTKn");

            //Habilitar escrita para a conexão
            connection.setDoOutput(true);

            //corpo da requisição
            String requestBody = "{\n" +
                    "  \"search\": {\n" +
                    "    // ... conteúdo do corpo da requisição ... \n" +
                    "  },\n" +
                    "  \"assessing\": {\n" +
                    "    // ... conteúdo do corpo da requisição ... \n" +
                    "  },\n" +
                    "  \"more_filters\": {\n" +
                    "    // ... conteúdo do corpo da requisição ... \n" +
                    "  }\n" +
                    "}";

                    //Enviar o corpo da requisição
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    //Obter a resposta
                    int responseCode = connection.getResponseCode();
                    System.out.println("Código de resposta: " + responseCode);

                    //Fechar a conexão
                    connection.disconnect();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class valuationstatusCliente {
    public static void main(String[] args) {
        try {
            //URL API
            String apiUrl = "https://api.prd.valuation.eemovel.com.br/valuation/assessment/internal/status/d48c1cc4667d47f4939e4867818acf32";

            //Cria uma URL a partir da string da URL da API
            URL url = new URL(apiUrl);

            //Abre uma conexão HTTP com a URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //Define o método de requisição (GET)
            connection.setRequestMethod("GET");

            //Define o header x-api-key com a chave fornecida
            connection.setRequestProperty("x-api-key", "TAwbnRfQeH4Yg6mB6YZsk42tTKn");

            //Obtém o código de resposta da requisição
            int responseCode = connection.getResponseCode();

            //Se a resposta for bem-sucedida (código 200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //lê a resposta da API
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null ) {

                }
                in.close();

                //Imprime a resposta da API (status da avaliação)
                System.out.println("Status da avaliação: " + response.toString());
            } else {
                System.out.println("Erro na requisição. Codigo de Resposta: " + responseCode);
            }

            //Fecha a conexão
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class APICliente {
    public static void main(String[] args) {
        try {
            //Rota da aPI
            String apiUrl = "https://api.prd.valuation.eemovel.com.br/valuation/reports/public/avm/d48c1cc4667d47f4939e4867818acf32";

            //Cria uma URL a partir da string da URL da API
            URL url = new URL(apiUrl);

            //Abre uma conexão HHTP com a URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //Define o método de requisição (GET)
            connection.setRequestMethod("GET");

            //Define o cabeçalho x-api-key
            connection.setRequestProperty("x-api-key", "TAwbnRfQeH4Yg6mB6YZsk42tTKnujdefsgrfd8");

            //Lê a resposta da API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //Imprime a resposta da API
            System.out.println("Resposta da API: " + response.toString());

            //Fecha a conexão
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class APIRequestAndConvertToPDF {
    private static final String toPath = null;

	public static void main(String[] args, Object toPath) {
        try {
            //URL da API
            String apiUrl = "https://api.prd.valuation.eemovel.com.br/valuation/reports/public/calculator/d48c1cc4667d47f4939e4867818acf32";

            //Define a chave da API
            String apikey = "TAwbnRfQeH4Yg6mB6YZsk42tTKn";

            //Cria uma URL a partir da string da URL da API
            URL url = new URL(apiUrl);

            //Abre uma conexão HTTP com a URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //Define o método de requisição (GET)
            connection.setRequestMethod("GET");

            //Obtém a resposta da API
            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {
                //Lê a resposta da API como um InputStream
                InputStream inputStream = connection.getInputStream();

                //Cria um arquivo temporário para armazenar os dados
                File tempfile = File.createTempFile("api_response", ".base64");

                //Copia os dados do InputStream para o arquivo temporário
                Files.copy(inputStream, tempfile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                //Fecha os fluxos
                inputStream.close();
                connection.disconnect();

				//Converte o conteudo base64 para um array de bytes
                byte[] base64Bytes = Files.readAllBytes(tempfile.toPath());

                //Cria um arquivo PDF
                File pdfFile = new File("api_response.pdf");

                //Escreve os dados decodificados no arquivo PDF
                FileOutPutStream pdfOutputStream = new FileOutPutStream(pdfFile);
                pdfOutputStream.write(base64Bytes);
                pdfOutputStream.close();

                System.out.println("Arquivo PDF gerado com sucesso: " + pdfFile.getAbsolutePath());
            } else {
                System.out.println("Falha na requisição. Cédigo de resposta: " + responseCode);
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class APICliente1 {
    public static void main(String[] args) {
        String url = "https://avm.valuation.eemovel.com.br/laudo/3685590d9e8441a19218e919bc52346b";
        HttpClient httpclient = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                //Lê o conteúdo da resposta
                String responseBody = EntityUtils.toString(response.getEntity());

                //link pdf pagina
                String pdfUrl = "https://avm.valuation.eemovel.com.br/laudo/download-pdf";

                //faz uma nova requisição para baixar o pdf
                HttpGet pdfRequest = new HttpGet(pdfUrl);
                HttpResponse pdfResponse = httpclient.execute(pdfRequest);
                byte[] pdfBytes = EntityUtils.toByteArray(pdfResponse.getEntity());

                //salva o pdf em um arquivo
//                try (OutputStream outputStream = new OutputStream("laudo.pdf")) {
//                    outputStream.write(pdfBytes);
//                }

                System.out.println("PDF baixado com sucesso.");
            } else {
                System.out.println("Erro na requisisção. código de status: " + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class APIClientGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton downloadButton;
	private String setApiVersion;

    public APIClientGUI() {
        setTitle("API Client GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 150);
        setLayout(new FlowLayout());

        downloadButton = new JButton("Baixar Laudo PDF");
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chame o código da API aqui
                APIClient.downloadPDF();
                JOptionPane.showMessageDialog(APIClientGUI.this, "PDF baixado com sucesso!");
            }
        });

        add(downloadButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            APIClientGUI gui = new APIClientGUI();
            gui.setVisible(true);
        });
    }

public String getApiVersion() {
		return getApiVersion();
	}

public void setApiVersion(String apiVersion) {
		setApiVersion = apiVersion;
	}
}