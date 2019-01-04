package playground.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import playground.jpadal.ActivityDao;
import playground.jpadal.ElementDao;
import playground.jpadal.UserDao;
import playground.logic.Entities.ActivityEntity;
import playground.logic.Entities.ElementEntity;
import playground.logic.Entities.UserEntity;
import playground.logic.jpa.ElementNotFoundException;
import playground.logic.jpa.UserNotFoundException;

@Component
public class ReadFromBoardActivityPlugin implements PlaygroungActivityPlugin {
	
	private UserDao users;
	private ElementDao elements;
	private ActivityDao activities;
	private ObjectMapper jackson;
	
	@Autowired
	public ReadFromBoardActivityPlugin(UserDao users,ElementDao elements, ActivityDao activities) {
		this.users = users;
		this.elements = elements;
		this.activities = activities;
		this.jackson = new ObjectMapper();
	} 
	
	@Override
	public ElementEntity checkAction(ActivityEntity activity) {
		String element_key = activity.getElementPlayground() + "@@" +activity.getElementId();
		ElementEntity element = this.elements.findById(element_key)
		.orElseThrow(()->new ElementNotFoundException("no Element for: " + element_key));
		
		if(!"Board".equalsIgnoreCase(element.getType()))
			throw new RuntimeException("Not an Animal!");
		
		return element;
	}
	
	@Override
	public Object invokeAction(ActivityEntity activity, String activityId, ElementEntity element) {
		try {
			Map<String,Integer> mapOfSizeAndPage = getSizeAndPage(activity);
			String mapOfSizeAndPageJson = this.jackson.writeValueAsString(mapOfSizeAndPage);
			
			PluginPageable page = this.jackson.readValue(mapOfSizeAndPageJson, PluginPageable.class); 
			List<ActivityEntity> activitiesWithMessages = this.activities
						.findAllByTypeLikeAndElementIdLikeAndElementPlaygroundLike("PostMessage", element.getId(),
								element.getPlayground(), PageRequest.of(page.getPage(), page.getSize())).getContent();
			
			return
				new ReadFromBoardResult(
					activitiesWithMessages
						.stream()
						.map(act -> (HashMap<String, Object>)act.getAttributes())
						.map(attributes -> (String)attributes.get("message"))
						.collect(Collectors.toList()), activityId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		
	}
	
	
	private Map<String,Integer> getSizeAndPage(ActivityEntity activity)
	{
		Map<String,Integer> mapOfSizeAndPage = new HashMap<String, Integer>();
		String size = "size";
		int defultSize = 10;
		String page = "page";
		int defultPage = 0;
		
		if(mapOfSizeAndPage.containsKey(size))
		{
			mapOfSizeAndPage.put(size, (Integer) activity.getAttributes().get(size));
		}
		else
		{
			mapOfSizeAndPage.put(size, defultSize);
		}
		
		
		if(mapOfSizeAndPage.containsKey(page))
		{
			mapOfSizeAndPage.put(page, (Integer) activity.getAttributes().get(page));
		}
		else
		{
			mapOfSizeAndPage.put(page, defultPage);
		}
		
		
		return mapOfSizeAndPage;
	}

}
