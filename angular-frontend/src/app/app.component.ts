import { Component } from '@angular/core';
import { HttpClient, HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  
  title = 'Upload Image';

  constructor(private httpClient: HttpClient){}

  selectedFile: File;
  retrievedImage: any;
  base64Data: any;
  retrieveResponse: any;
  message: string;
  imageName: any;

  //gets called when the user selects an image
  public onFileChanged(event){
    //select file
    console.log(event.target.files[0]);
    this.selectedFile = event.target.files[0];
  }

  //gets called when the user clicks on submit button to upload anything file .jpg, png, gif for thumbnail
  onUpload(){
    console.log(this.selectedFile);

    const uploadImageData = new FormData();
    uploadImageData.append('imageFile', this.selectedFile, this.selectedFile.name);

  
    this.httpClient.post('http://localhost:8080/image/upload', uploadImageData, {
      observe: 'response'
    }).subscribe((response) => {
      if(response.status === 200){
        this.message = 'Image uploaded successfully';
      }else{
        this.message = 'Image not upload successfully';
      }
    });
  }

  //get the called when the user click om retrieve image button to get the image from backend
  getImage(){
    this.httpClient.get('http://localhost:8080/image/get-image/'+this.imageName)
      .subscribe(res => {
        this.retrieveResponse = res;
        //payload picByte
        this.base64Data = this.retrieveResponse.picByte;
        this.retrievedImage = 'data:image/jpeg;base64,'+this.base64Data;
      });
  }
}
