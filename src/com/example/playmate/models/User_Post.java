package com.example.playmate.models;

public class User_Post {
	
	private String Id,UserName,PhoneNumber,ProfilePic,Post,IMG_URL,Distance;	

	 public void setId(String Id){
	    this.Id= Id;
	 }

	 public String getUserName(){
	    return this.UserName;
	 }
	 
	 public void setUserName(String UserName){
		    this.UserName=UserName;
		 }
	 
	 public void setImage(String IMG_URL){
		    this.IMG_URL=IMG_URL;
		 }
	 
	 public void setPhoneNumber(String PhoneNumber){
		    this.PhoneNumber = PhoneNumber;
		 }
	 public void setDistance(String Distance){
		    this.Distance = Distance;
		 }
	 
	 public String getPost(){
		    return this.Post;
		 }
	 
	 public void setPost(String Post){
		    this.Post = Post;
		 }
	 
	 public String getPhoneNumber(){
		    return this.PhoneNumber;
		 }
	 
	 public String getDistance(){
		    return this.Distance;
		 }

		 public String getProfilePic(){
		    return this.ProfilePic;
		 }
		 
		 public String getId(){
			    return this.Id;
			 }

		public String getImage() {
			// TODO Auto-generated method stub
			return this.IMG_URL;
		}	 
}
