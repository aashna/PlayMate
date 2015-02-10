package com.example.playmate.models;

public class User_Post {
	
	private String PostId,UserName,PhoneNumber,ProfilePic,Post,IMG_URL_PROFILE,IMG_URL_POSTED,Distance,Time,Favs,Comments,PostType;	

	 public void setPostId(String PostId){
	    this.PostId= PostId;
	 }

	 public String getUserName(){
	    return this.UserName;
	 }
	 
	 public void setUserName(String UserName){
		    this.UserName=UserName;
		 }
	 
	 public void setProfileImage(String IMG_URL){
		    this.IMG_URL_PROFILE=IMG_URL;
		 }
	 public void setPostedImage(String IMG_URL){
		    this.IMG_URL_POSTED=IMG_URL;
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
	 
	 public void setTime(String Time){
		    this.Time = Time;
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
		 
		 public String getPostId(){
			    return this.PostId;
			 }
		 
		 public String getTime(){
			    return this.Time;
			 }

		public String getProfileImage() {
			// TODO Auto-generated method stub
			return this.IMG_URL_PROFILE;
		}	
		
		public String getPostedImage() {
			// TODO Auto-generated method stub
			return this.IMG_URL_POSTED;
		}
		
		 public void setFavs(String Favs){
			    this.Favs= Favs;
			 }

			 public String getFavs(){
			    return this.Favs;
			 }
			 
			 public void setComments(String Comments){
				    this.Comments= Comments;
				 }

				 public String getComments(){
				    return this.Comments;
				 }
				 
				 public void setPostType(String PostType){
					    this.PostType= PostType;
					 }

					 public String getPostType(){
					    return this.PostType;
					 }
}
