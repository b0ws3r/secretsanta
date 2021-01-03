# secretsanta

This project allows secret santa matching for a group of people. The Java project runs and exposes an API that takes in participant information and exclusions using a post request. 

Suppose there are 4 participants in the group: Alison, Benedict, Charlene, and Drew. If Alison lives with Ben, it wouldn't be as fun if Alison was matched to Ben for secret santa. 
Thus, you can specify that the algorithm cannot match Alison to Ben, or Ben to Alison. 

The initial strategy to handle this is to split the participants into two groups. Someone from group A will be matched to group B, and then that person from group B will be matched to the next
person in group A. If we ensure that the people that shouldn't be matched to each other are in the same group, then we are guaranteed that Alison and Ben will not be matched to eachother. 

There are two exceptions to this working: 
- If the group is 3 people or less
- If there is an odd number

The second scenario can be handled if we make sure that in the case of an odd number of participants, that the participants are ordered in such a way that the last person to be matched is not matched to the first person in their group. 

A -----> B<br/>
C <----- B<br/>
C -----> D<br/>
A <----- D<br/>
