package pack.jetminister.data;

public class Image {

//    private String mName;
    private String mImageUrl;

    public Image(){
        //Empty constructor needed
    }

    public Image(String ImageUrl){
//        if(name.trim().equals("")){
//            name = "No Name";
//        }
//
//        mName = name;
        mImageUrl = ImageUrl;
    }

//    public String getName(){
//        return mName;
//    }

//    public void setName(String name) {
//        //TODO place here a call to username from database to make image name
//        mName = "";
//    }

    public String getmImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        mImageUrl = ImageUrl;
    }

}
