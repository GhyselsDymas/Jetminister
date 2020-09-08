package pack.jetminister.data;

public class Image {

    private String name;
    private String imageURL;

    public Image(){
        //Empty constructor needed
    }

    public Image(String ImageUrl, String name){
//        if(name.trim().equals("")){
//            name = "No Name";
//        }
//
//        mName = name;
        imageURL = ImageUrl;
    }

//    public String getName(){
//        return mName;
//    }

//    public void setName(String name) {
//        //TODO place here a call to username from database to make image name
//        mName = "";
//    }

    public String getImageURL(){
        return imageURL;
    }

    public void setImageUrl(String ImageUrl) {
        imageURL = ImageUrl;
    }

}
