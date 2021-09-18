package Technical;


import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;

public class Sort {
    
    public static LinkedHashMap<String,LinkedHashMap<Integer,Double>> getSortedUserIdAndTheValuesListInAllSAs(LinkedHashMap<String, LinkedHashMap<Integer,Double>> SA__userId_Value, String orderType, double valueType){
        LinkedHashMap<String,LinkedHashMap<Integer,Double>> SA__SortedUId_Value = new LinkedHashMap<>();
        for(Map.Entry<String, LinkedHashMap<Integer,Double>> SAItem : SA__userId_Value.entrySet()){
            String SA = SAItem.getKey();
            LinkedHashMap<Integer,Double> UId_Value = SAItem.getValue();
            LinkedHashMap<Integer,Double> SortedUId_Value = Sort.getSortedUserIdAndTheValuesList(UId_Value, orderType, valueType);
            SA__SortedUId_Value.put(SA, SortedUId_Value);
        }
        return SA__SortedUId_Value;
    }
        
    public static LinkedHashMap<Integer,Double> getSortedUserIdAndTheValuesList(LinkedHashMap<Integer,Double> userId_Value, String orderType, double valueType){
        LinkedHashMap<Integer, Double> sortedUserId_value = new LinkedHashMap<>();
        LinkedHashSet<Integer> SortedUserIdList = getSortedUserId(userId_Value, orderType, valueType);
        for(int userId : SortedUserIdList){
            sortedUserId_value.put(userId, userId_Value.get(userId));
        }
        return sortedUserId_value;
    }
        
    private static LinkedHashSet<Integer> getSortedUserId(LinkedHashMap<Integer,Double> userId_Value, String orderType, double valueType){
        LinkedHashSet<Integer> SortedUserIdList = new LinkedHashSet<>();
        TreeMap<Double,HashSet<Integer>> value_userIdList = getUserIdListBasedOnSortedValues(userId_Value, orderType,(double)valueType);
        for(Map.Entry<Double,HashSet<Integer>> Item: value_userIdList.entrySet()){
            HashSet<Integer> userIdList = (HashSet<Integer>) Item.getValue().clone();
            SortedUserIdList.addAll(userIdList);
        }
        return SortedUserIdList;
    }

    private static TreeMap<Double, HashSet<Integer>> getUserIdListBasedOnSortedValues(LinkedHashMap<Integer,Double> userId_Value, String orderType, double valueType) {
        TreeMap<Double,HashSet<Integer>> value_userIdList;
        value_userIdList=(orderType.equals("DESC")?new TreeMap<>(Collections.reverseOrder()):new TreeMap<>()) ;
        for(Map.Entry<Integer,Double> userIdItem: userId_Value.entrySet()){
            int userId = userIdItem.getKey();
            double value = userIdItem.getValue();
            if(value_userIdList.containsKey(value)){
                HashSet<Integer> userIdList = value_userIdList.get(value);
                userIdList.add(userId);
                value_userIdList.replace(value, userIdList);
            }else{
                HashSet<Integer> userIdList = new HashSet<>();
                userIdList.add(userId);
                value_userIdList.put(value, userIdList);
            }
        }
        return value_userIdList;
    }
    
}
