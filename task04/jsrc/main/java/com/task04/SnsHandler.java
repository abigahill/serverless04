package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.RegionScope;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.annotations.events.SnsEventSource;
import com.syndicate.deployment.annotations.resources.DependsOn;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "sns_handler",
	roleName = "sns_handler-role",
	isPublishVersion = false,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SnsEventSource(
	targetTopic="lambda_topic",
	regionScope=RegionScope.DEFAULT
)
@DependsOn(
	name = "lambda_topic",
	resourceType = ResourceType.SNS_TOPIC
)
public class SnsHandler implements RequestHandler<SNSEvent, Map<String, Object>> {

	public Map<String, Object> handleRequest(SNSEvent event, Context context) {
		LambdaLogger logger = context.getLogger();
		Map<String, Object> response = new HashMap<String, Object>();
		try{
			event.getRecords().forEach(
					record -> logger.log("Message received from SNS: " + record.getSNS().getMessage()));
		} catch (Exception e) {
			response.put("statusCode", 500);
			response.put("message", "Something wrong happened: " + e.getMessage());
			return response;
		}
		response.put("statusCode", 200);
		response.put("message", "End processing SNS event!");
		return response;
	}
}
