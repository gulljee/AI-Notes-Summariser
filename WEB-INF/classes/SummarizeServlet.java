import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/summarize")
@MultipartConfig
public class SummarizeServlet extends HttpServlet{

    private static final String API_URL = "https://api-inference.huggingface.co/models/facebook/bart-large-cnn";
    private static final String API_TOKEN = "Enter your API key;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        req.setCharacterEncoding("UTF-8");
        Part filePart = req.getPart("file");
        BufferedReader reader = new BufferedReader(new InputStreamReader(filePart.getInputStream()));

        
        StringBuilder inputText = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null){
            inputText.append(line).append("\n");
        }
        reader.close();

        String cleanedText = inputText.toString().replace("\n"," ");
        String jsonInput = "{\"inputs\":\"" + cleanedText.toString().replace("\"","\\\"")+"\"}";

        System.out.println("INPUT TEXT:\n" + inputText.toString());
        System.out.println("JSON INPUT:\n" + jsonInput);

        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization","Bearer " + API_TOKEN);
        conn.setRequestProperty("Content-Type","application/json");
        conn.setDoOutput(true);

        try(OutputStream os = conn.getOutputStream()){
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input,0,input.length);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        StringBuilder result = new StringBuilder();

        try(BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"))){
            String responseLine;

            while((responseLine = br.readLine()) != null){
                result.append(responseLine.trim());
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
     res.setContentType("application/json");
     res.setCharacterEncoding("UTF-8");

     res.getWriter().write(result.toString());   
    
    } 
}