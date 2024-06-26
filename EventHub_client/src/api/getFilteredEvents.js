import axios from "./axios";
import queryString from "query-string";
const FILTER_URL = "events/filter";

export const getFilteredEvents = async () => {
  const parsed = queryString.parse(window.location.search);

  // to send correctly categories
  if ("category_requests" in parsed) {
    if (Array.isArray(parsed.category_requests)) {
      parsed.category_requests = parsed.category_requests.map((category) => ({
        name: category,
      }));
    } else {
      parsed.category_requests = [{ name: parsed.category_requests }];
    }
  } else {
    parsed.category_requests = [];
  }

  //to send correctly date
  if (parsed.start_at.trim() !== "") {
    parsed.start_at = new Date(parsed.start_at);
  }

  if (parsed.expire_at.trim() !== "") {
    parsed.expire_at = new Date(parsed.expire_at);
  }

  const accessToken = localStorage.getItem("token");

  const authAxios = axios.create({
    headers: {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Headers": "content-type",
      "Access-Control-Allow-Credentials": "true",
      ...(accessToken && { Authorization: `Bearer ${accessToken}` }),
    },
  });

  try {
    const response = await authAxios.post(FILTER_URL, parsed);
    return response.data;
  } catch (error) {
    console.log("Error getting filtred events", error);
  }
};
