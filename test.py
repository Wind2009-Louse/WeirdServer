import json
import requests

def main():
	url = "http://127.0.0.1:8080/search_3"
	# name=e&dept=u&sort=id&desc&lower=100&upper=800&id=55739&page=-1
	headers = {"Content-Type":"application/json"}
	body = {
		#"id" : "55739",
		#"name" : "e",
		#"dept" : "u",
		#"page" : "-1",
		#"sort" : "dept",
		#"desc" : "a",
		"lower" : "100",
		"upper" : "800"
	}
	print(requests.post(url, headers=headers, data=json.dumps(body)).text)

if __name__ == "__main__":
	main()