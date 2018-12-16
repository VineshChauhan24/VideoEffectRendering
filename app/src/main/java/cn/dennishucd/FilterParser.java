package cn.dennishucd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class FilterParser {
	
	public class FilterInfo {
		public float[] filterMatrix;
		public int[] holoColorArray;
		public float[] holoPosArray;
		public String holoModeName;
	}
	
	HashMap<String, FilterInfo> idFilterInfo;
	
	public FilterParser(String filterspath) {		
		try {
			InputStream is = new FileInputStream(filterspath + "/filters.json");
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));					
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + " ");
				}					
				String json = sb.toString();
		        
				JSONArray objArray = new JSONObject(json).getJSONArray("filters");
				idFilterInfo = new HashMap<String, FilterInfo>();
				
				for(int i = 0; i < objArray.length(); i++) {
					JSONObject obj = objArray.getJSONObject(i);
					
					String id = obj.getString("id");
					
					FilterInfo filterinfo = new FilterInfo();					
					if(!obj.isNull("filter")) {
						JSONArray objArray1 = obj.getJSONArray("filter");
						filterinfo.filterMatrix = new float[objArray1.length()];
						for(int j = 0; j < objArray1.length(); j++)
							filterinfo.filterMatrix[j] = (float)objArray1.getDouble(j);

					}
					
					if(!obj.isNull("holoColor")) {
						JSONArray objArray2 = obj.getJSONArray("holoColor");
						filterinfo.holoColorArray = new int[objArray2.length()];
						for(int j = 0; j < objArray2.length(); j++) {
							filterinfo.holoColorArray[j] = Long.decode(objArray2.getString(j)).intValue();
						}
					}
					
					if(!obj.isNull("holoPos")) {
						JSONArray objArray3 = obj.getJSONArray("holoPos");
						filterinfo.holoPosArray = new float[objArray3.length()];
						for(int j = 0; j < objArray3.length(); j++)
							filterinfo.holoPosArray[j] = (float)objArray3.getDouble(j);
					}
					
					if(!obj.isNull("holoMode")) {
						filterinfo.holoModeName = obj.getString("holoMode");
					}
					

					idFilterInfo.put(id, filterinfo);
 	
				}//for i
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public FilterParser(Context context, String filterspath) {		
		try {
			InputStream is = context.getAssets().open(filterspath + "/filters.json");

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));					
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + " ");
				}					
				String json = sb.toString();
		        
				JSONArray objArray = new JSONObject(json).getJSONArray("filters");
				idFilterInfo = new HashMap<String, FilterInfo>();
				
				for(int i = 0; i < objArray.length(); i++) {
					JSONObject obj = objArray.getJSONObject(i);
					
					String id = obj.getString("id");
				
					
					FilterInfo filterinfo;
					if(!obj.isNull("origin") && obj.getBoolean("origin"))
						filterinfo = null;
					else {
						filterinfo = new FilterInfo();					
						if(!obj.isNull("filter")) {
							JSONArray objArray1 = obj.getJSONArray("filter");
							filterinfo.filterMatrix = new float[objArray1.length()];
							for(int j = 0; j < objArray1.length(); j++)
								filterinfo.filterMatrix[j] = (float)objArray1.getDouble(j);
	
						}
						
						if(!obj.isNull("holoColor")) {
							JSONArray objArray2 = obj.getJSONArray("holoColor");
							filterinfo.holoColorArray = new int[objArray2.length()];
							for(int j = 0; j < objArray2.length(); j++) {
								filterinfo.holoColorArray[j] = Long.decode(objArray2.getString(j)).intValue();
							}
						}
						
						if(!obj.isNull("holoPos")) {
							JSONArray objArray3 = obj.getJSONArray("holoPos");
							filterinfo.holoPosArray = new float[objArray3.length()];
							for(int j = 0; j < objArray3.length(); j++)
								filterinfo.holoPosArray[j] = (float)objArray3.getDouble(j);
						}
						
						if(!obj.isNull("holoMode")) {
							filterinfo.holoModeName = obj.getString("holoMode");
						}

					}

					idFilterInfo.put(id, filterinfo);
 	
				}//for i
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public HashMap<String, FilterInfo> getIdFilterInfo() {
		return idFilterInfo;
	}

}
