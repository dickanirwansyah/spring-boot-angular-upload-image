package id.backend.springbootbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping(value = "/image")
public class ImageModelController {

    @Autowired
    private ImageModelRepository imageModelRepository;

    //compress the image byte before storing it in the database
    public static byte[] compressByte(byte[] data){
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()){
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try{
            outputStream.close();
        }catch (IOException e){}

        System.out.println("Compressed image byte size - "+outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    //uncompress the image bytes before returning it to the angular frontend application
    public static byte[] decompressByte(byte[] data){
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try{
            while (!inflater.finished()){
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        }catch (IOException e){

        }catch (DataFormatException e){

        }
        return outputStream.toByteArray();
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<Object> uploadImage(@RequestParam(value = "imageFile")
                                                  MultipartFile imageFile) throws IOException {

        System.out.println("Original Image Byte Size - "+imageFile.getBytes().length);
        ImageModel imageModel = ImageModel.builder()
                .name(imageFile.getOriginalFilename())
                .type(imageFile.getContentType())
                .picByte(compressByte(imageFile.getBytes()))
                .build();
        imageModelRepository.save(imageModel);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/get-image/{imageName}")
    public ImageModel getImage(@PathVariable("imageName")String imageName) throws IOException{
        final Optional<ImageModel> retrievedImage = imageModelRepository.findByName(imageName);
        ImageModel imageModel = ImageModel.builder()
                .id(retrievedImage.get().getId())
                .name(retrievedImage.get().getName())
                .type(retrievedImage.get().getType())
                .picByte(decompressByte(retrievedImage.get().getPicByte()))
                .build();
        return imageModel;
    }
}
