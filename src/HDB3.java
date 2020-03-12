
public class HDB3 {
	public static String decodeHdb3(String str)
	{
		String newstr="";
		int i=str.length()-1;
		String chr;
		while(i>=0)
		{
		if((str.length()==1)&&(str.substring(0,1).equals("0")))
		{
			newstr="0"+newstr;
			i--;
		}
		else if((str.length()==1)&&(str.substring(0,1).equals("+")))
		{
			newstr="1"+newstr;
			i--;
		}
		else if((str.length()==1)&&(str.substring(0,1).equals("-")))
		{
			newstr="1"+newstr;
			i--;
		}
		else if(str.length()<4)
		{
			
				chr = str.substring(str.length()-1,str.length());
				if(chr.equals("0"))
				{
					newstr="0"+newstr;
					str=str.substring(0,str.length()-1);
					i--;
				}
				else
				{
					newstr="1"+newstr;
					str=str.substring(0,str.length()-1);
					i--;
				}
				
		}
		else
		{
			
			if(str.substring(str.length()-4, str.length()).equals("000+")||str.substring(str.length()-4, str.length()).equals("000-")||str.substring(str.length()-4, str.length()).equals("-00-")||str.substring(str.length()-4, str.length()).equals("+00+"))
				{
					newstr="0000"+newstr;
					str=str.substring(0,str.length()-4);
					i=i-4;
				}
			else
				{
					
					chr = str.substring(str.length()-1, str.length());
					if(chr.equals("0"))
					{
						newstr="0"+newstr;
						str=str.substring(0,str.length()-1);
					}
					else
					{
						newstr="1"+newstr;
						str=str.substring(0,str.length()-1);
					}
					i--;
				}
							
			}//while loop end
		}//else end
			
		return newstr;
		}

	
	public static String convertHDB3(String str)
	{
		int counter0=0;
		int counter1=0;
		int positiveOrNegative=1;
		int chr;
		String newstr="";
		String tempt="";
		for(int i=0; i<str.length();i++)
		{
			
			chr = str.charAt(i);
			
			if(chr==48)
			{
				if(counter0<3)
				{
					counter0++;
					newstr=newstr + "0";
				}
				else
				{
					counter0=0;
					if(newstr.length()<4)
					{
						newstr = "";
					}
					else
					{
						newstr = newstr.substring(0, newstr.length()-3);
					}
					
					if(counter1==0&&positiveOrNegative==1)
					{
						tempt="-00-";
						positiveOrNegative=-1;
					}
					else if(counter1==0&&positiveOrNegative==-1)
					{
						tempt="+00+";
						positiveOrNegative=1;
					}
					else if(counter1==1&&positiveOrNegative==1)
					{
						tempt="000+";
						positiveOrNegative=1;
						counter1=0;
					}
					else if(counter1==1&&positiveOrNegative==-1)
					{
						tempt="000-";
						positiveOrNegative=-1;
						counter1=0;
					}
					else
					{
						;
					}
					
					newstr=newstr+tempt;
					
				}//else end

			}//chr48 end	
			 if(chr==49)
			 {
				 counter0=0;
				 
				 if(counter1==0)
					 counter1=1;
				 else
					 counter1=0;
				 
				 if(positiveOrNegative==1)
				 {
					 positiveOrNegative=-1;
					 newstr=newstr + "-";
				 }
				 
				 else
				 {
					 positiveOrNegative=1;
					 newstr=newstr + "+";
				 }
				 
				
			 }
			
		}
		return newstr;
	}
}
