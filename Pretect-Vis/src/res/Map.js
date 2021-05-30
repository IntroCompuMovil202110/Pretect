import { feature } from "topojson-client";
import countries from "./land10.json";
export const COUNTRIES = feature(countries, countries.objects.land).features;