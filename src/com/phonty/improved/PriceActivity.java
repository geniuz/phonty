/*
 * Copyright (C) 2012 PhontyCom - Belize
 * 
 * This file is part of Phonty(http://www.phonty.com/android)
 * 
 * Phonty is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.phonty.improved;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.phonty.improved.PhontyService.Tasks;
import com.phonty.improved.R;
import com.phonty.improved.Rates.PriceItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PriceActivity extends Activity {
	private Button showButton;
    private ProgressDialog progress;
    private Boolean taskIsRunning = false;
	private RatesTask task;
	private String country;
	private ListView countriesList,countriesSmsList;
	private AutoCompleteTextView countryEdit; 
	Intent intent;
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.price);

	     ArrayAdapter<String> adapter = new ArrayAdapter<String>
	     	(this,android.R.layout.simple_dropdown_item_1line,countries);

	     final AutoCompleteTextView countryEdit = (AutoCompleteTextView)findViewById(R.id.countryEdit);
	     countryEdit.setThreshold(1);
	     countryEdit.setAdapter(adapter);

	     showButton = (Button)findViewById(R.id.show);
	     countriesList = (ListView)findViewById(R.id.countriesList);
	     countriesList.setCacheColorHint(Color.TRANSPARENT);
	     

	     showButton.setOnClickListener(new OnClickListener(){
	      @Override
	      public void onClick(View arg0) {
	    	  country = countryEdit.getText().toString();
	    	  progress = ProgressDialog.show(PriceActivity.this, getString(R.string.processing), getString(R.string.newAccountTip), true,false);
              task = new RatesTask();
              task.execute();
	      	}
	      });
	}
	     	 
	 @Override
	 public void onResume() {
		 super.onResume();
	 }
	
	 @Override
	 public void onPause() {
		 super.onPause();
	 }
	 

	 class RatesTask extends AsyncTask<Tasks, String, Boolean>  {
		ArrayList<PriceItem> callsRatesList,smsRatesList;
		boolean result,smsresult;
		
		protected Boolean doInBackground(Tasks... action) {
			if (taskIsRunning) { this.cancel(true); return null; }
			taskIsRunning = true;
 			Rates rates = new Rates(getResources().getText(R.string.rates_url).toString(),getApplicationContext());
			result = rates.get(country);
 			rates.getsms(country,getResources().getText(R.string.sms_rates_url).toString());
			callsRatesList = rates.VALUES; 			 			
 			return result;
		}
		protected void onProgressUpdate(Integer... progress) {}
		protected void onPostExecute(Boolean result) {
			taskIsRunning = false;
			progress.dismiss();
			Log.e("ARRAY",callsRatesList.toString());
			if (result) {
			     ListAdapter directions = new SimpleAdapter(PriceActivity.this,
			    		 	callsRatesList,
			    		 	R.layout.countries_view,
			    		 	new String[] {PriceItem.NAME,PriceItem.AMOUNT},
			    		 	new int[]{R.id.countryName, R.id.amount});
			     countriesList.setAdapter(directions);
			}
		}
	}
	 
	 

	 String[] countries = {
		"Abkhazia",
		"Andorra",
		"United Arab Emirates",
		"Afghanistan",
		"Antigua and Barbuda",
		"Anguilla",
		"Albania",
		"Armenia",
		"Netherlands Antilles",
		"Angola",
		"Antarctica",
		"Argentina",
		"American Samoa",
		"Austria",
		"Australia",
		"Aruba",
		"Aland Islands",
		"Azerbaijan",
		"Bosnia and Herzegovina",
		"Barbados",
		"Bangladesh",
		"Belgium",
		"Burkina Faso",
		"Bulgaria",
		"Bahrain",
		"Burundi",
		"Benin",
		"Saint Barthelemy",
		"Bermuda",
		"Brunei",
		"Bolivia",
		"Brazil",
		"Bahamas",
		"Bhutan",
		"Bouvet Island",
		"Botswana",
		"Belarus",
		"Belize",
		"Canada",
		"Cocos (Keeling) Islands",
		"Congo (Democratic Republic)",
		"Central African Republic",
		"Congo",
		"Switzerland",
		"Cote d’Ivoire",
		"Cook Islands",
		"Chile",
		"Cameroon",
		"China",
		"Colombia",
		"Costa Rica",
		"Cuba",
		"Cape Verde",
		"Christmas Island",
		"Cyprus",
		"Czech Republic",
		"Germany",
		"Djibouti",
		"Denmark",
		"Dominica",
		"Dominican Republic",
		"Algeria",
		"Ecuador",
		"Estonia",
		"Egypt",
		"Western Sahara",
		"Eritrea",
		"Ceuta",
		"Melilla",
		"Spain",
		"Ethiopia",
		"European Union",
		"Finland",
		"Fiji",
		"Falkland Islands (Malvinas)",
		"Micronesia",
		"Faroe Islands",
		"France",
		"Gabon",
		"United Kingdom",
		"Grenada",
		"Georgia",
		"French Guiana",
		"Guernsey",
		"Ghana",
		"Gibraltar",
		"Greenland",
		"Gambia",
		"Guinea",
		"Guadeloupe",
		"Equatorial Guinea",
		"Greece",
		"South Georgia and the South Sandwich Islands",
		"Guatemala",
		"Guam",
		"Guinea-Bissa",
		"Guyana",
		"Hong Kong",
		"Heard Island and McDonald Islands",
		"Honduras",
		"Croatia",
		"Haiti",
		"Hungary",
		"Canary Islands",
		"Indonesia",
		"Ireland",
		"Israel",
		"Isle of Man",
		"India",
		"British Indian Ocean Territory",
		"Iraq",
		"Iran",
		"Iceland",
		"Italy",
		"Jersey",
		"Jamaica",
		"Jordan",
		"Japan",
		"Kenya",
		"Kyrgyzstan",
		"Cambodia",
		"Kiribati",
		"Comoros",
		"Saint Kitts and Nevis",
		"Kosovo",
		"North Korea",
		"South Korea",
		"Kuwait",
		"Cayman Islands",
		"Kazakhstan",
		"Laos",
		"Lebanon",
		"Saint Lucia",
		"Liechtenstein",
		"Sri Lanka",
		"Liberia",
		"Lesotho",
		"Lithuania",
		"Luxembourg",
		"Latvia",
		"Libya",
		"Morocco",
		"Monaco",
		"Moldova",
		"Montenegro",
		"Saint Martin",
		"Madagascar",
		"Marshall Islands",
		"Macedonia",
		"Mali",
		"Myanmar",
		"Mongolia",
		"Macao",
		"Northern Mariana Islands",
		"Martinique",
		"Mauritania",
		"Montserrat",
		"Malta",
		"Mauritius",
		"Maldives",
		"Malawi",
		"Mexico",
		"Malaysia",
		"Mozambique",
		"Namibia",
		"New Caledonia",
		"Niger",
		"Norfolk Island",
		"Nigeria",
		"Nicaragua",
		"Nagorno-Karabakh Republic",
		"Netherlands",
		"Norway",
		"Nepal",
		"Naur",
		"Niue",
		"New Zealand",
		"Oman",
		"Panama",
		"Per",
		"French Polynesia",
		"Papua New Guinea",
		"Philippines",
		"Pakistan",
		"Poland",
		"Saint Pierre and Miquelon",
		"Pitcairn",
		"Puerto Rico",
		"Palestinian Territory",
		"Portugal",
		"Pala",
		"Paraguay",
		"Qatar",
		"Reunion",
		"Romania",
		"Serbia",
		"Russia",
		"Rwanda",
		"Saudi Arabia",
		"Solomon Islands",
		"Seychelles",
		"Sudan",
		"Sweden",
		"Singapore",
		"Saint Helena",
		"Slovenia",
		"Svalbard and Jan Mayen",
		"Slovakia",
		"Sierra Leone",
		"San Marino",
		"Senegal",
		"Somalia",
		"South Ossetia",
		"Suriname",
		"Sao Tome and Principe",
		"El Salvador",
		"Syrian Arab Republic",
		"Swaziland",
		"Turks and Caicos Islands",
		"Chad",
		"French Southern Territories",
		"Togo",
		"Thailand",
		"Tajikistan",
		"Tokela",
		"Timor-Leste",
		"Turkmenistan",
		"Tunisia",
		"Tonga",
		"Turkey",
		"Trinidad and Tobago",
		"Tuval",
		"Taiwan",
		"Tanzania",
		"Ukraine",
		"Uganda",
		"United States Minor Outlying Islands",
		"United States",
		"Uruguay",
		"Uzbekistan",
		"Holy See (Vatican)",
		"Saint Vincent and the Grenadines",
		"Venezuela",
		"Virgin Islands (British)",
		"Virgin Islands (U.S.)",
		"Vietnam",
		"Vanuat",
		"Wallis and Futuna",
		"Samoa",
		"Yemen",
		"Mayotte",
		"South Africa",
		"Zambia",
		"Zimbabwe",
		"Абхазия",
		"Андорра",
		"Объединённые Арабские Эмираты",
		"Афганистан",
		"Антигуа и Барбуда",
		"Ангилья",
		"Албания",
		"Армения",
		"Нидерландские Антилы",
		"Ангола",
		"Антарктида",
		"Аргентина",
		"Американское Самоа",
		"Австрия",
		"Австралия",
		"Аруба",
		"Эландские острова",
		"Азербайджан",
		"Босния и Герцеговина",
		"Барбадос",
		"Бангладеш",
		"Бельгия",
		"Буркина Фасо",
		"Болгария",
		"Бахрейн",
		"Бурунди",
		"Бенин",
		"Сен-Бартельми",
		"Бермуды",
		"Бруней",
		"Боливия",
		"Бразилия",
		"Багамы",
		"Бутан",
		"Остров Буве",
		"Ботсвана",
		"Беларусь",
		"Белиз",
		"Канада",
		"Кокосовые (Килинг) острова",
		"Конго (Демократическая Республика)",
		"Центральноафриканская Республика",
		"Конго",
		"Швейцария",
		"Кот-д’Ивуар",
		"Острова Кука",
		"Чили",
		"Камерун",
		"Китай",
		"Колумбия",
		"Коста-Рика",
		"Куба",
		"Кабо-Верде",
		"Остров Рождества",
		"Кипр",
		"Чехия",
		"Германия",
		"Джибути",
		"Дания",
		"Доминика",
		"Доминиканская Республика",
		"Алжир",
		"Эквадор",
		"Эстония",
		"Египет",
		"Западная Сахара",
		"Эритрея",
		"Сеута",
		"Мельлия",
		"Испания",
		"Эфиопия",
		"Евросоюз",
		"Финляндия",
		"Фиджи",
		"Фолклендские острова (Мальвинские)",
		"Микронезия",
		"Фарерские острова",
		"Франция",
		"Габон",
		"Великобритания",
		"Гренада",
		"Грузия",
		"Французская Гвиана",
		"Гернси",
		"Гана",
		"Гибралтар",
		"Гренландия",
		"Гамбия",
		"Гвинея",
		"Гваделупа",
		"Экваториальная Гвинея",
		"Греция",
		"Южная Джорджия и Южные Сандвичевы острова",
		"Гватемала",
		"Гуам",
		"Гвинея-Бисау",
		"Гайана",
		"Гонконг",
		"Остров Херд и острова Макдональд",
		"Гондурас",
		"Хорватия",
		"Гаити",
		"Венгрия",
		"Канарские острова",
		"Индонезия",
		"Ирландия",
		"Израиль",
		"Остров Мэн",
		"Индия",
		"Британская территория в Индийском океане",
		"Ирак",
		"Иран",
		"Исландия",
		"Италия",
		"Джерси",
		"Ямайка",
		"Иордания",
		"Япония",
		"Кения",
		"Киргизия",
		"Камбоджа",
		"Кирибати",
		"Коморы",
		"Сент-Китс и Невис",
		"Косово",
		"Северная Корея",
		"Южная Корея",
		"Кувейт",
		"Острова Кайман",
		"Казахстан",
		"Лаос",
		"Ливан",
		"Сент-Люсия",
		"Лихтенштейн",
		"Шри-Ланка",
		"Либерия",
		"Лесото",
		"Литва",
		"Люксембург",
		"Латвия",
		"Ливия",
		"Марокко",
		"Монако",
		"Молдова",
		"Черногория",
		"Остров Святого Мартина",
		"Мадагаскар",
		"Маршалловы острова",
		"Македония",
		"Мали",
		"Мьянма",
		"Монголия",
		"Макао",
		"Северные Марианские острова",
		"Мартиника",
		"Мавритания",
		"Монтсеррат",
		"Мальта",
		"Маврикий",
		"Мальдивы",
		"Малави",
		"Мексика",
		"Малайзия",
		"Мозамбик",
		"Намибия",
		"Новая Каледония",
		"Нигер",
		"Остров Норфолк",
		"Нигерия",
		"Никарагуа",
		"Нагорно-Карабахская Республика",
		"Нидерланды",
		"Норвегия",
		"Непал",
		"Науру",
		"Ниуэ",
		"Новая Зеландия",
		"Оман",
		"Панама",
		"Перу",
		"Французская Полинезия",
		"Папуа-Новая Гвинея",
		"Филиппины",
		"Пакистан",
		"Польша",
		"Сен-Пьер и Микелон",
		"Питкерн",
		"Пуэрто-Рико",
		"Палестинская автономия",
		"Португалия",
		"Палау",
		"Парагвай",
		"Катар",
		"Реюньон",
		"Румыния",
		"Сербия",
		"Россия",
		"Руанда",
		"Саудовская Аравия",
		"Соломоновы острова",
		"Сейшелы",
		"Судан",
		"Швеция",
		"Сингапур",
		"Святая Елена",
		"Словения",
		"Шпицберген и Ян Майен",
		"Словакия",
		"Сьерра-Леоне",
		"Сан-Марино",
		"Сенегал",
		"Сомали",
		"Южная Осетия",
		"Суринам",
		"Сан-Томе и Принсипи",
		"Эль-Сальвадор",
		"Сирийская Арабская Республика",
		"Свазиленд",
		"Острова Теркс и Кайкос",
		"Чад",
		"Французские Южные территории",
		"Того",
		"Таиланд",
		"Таджикистан",
		"Токелау",
		"Тимор-Лесте",
		"Туркмения",
		"Тунис",
		"Тонга",
		"Турция",
		"Тринидад и Тобаго",
		"Тувалу",
		"Тайвань",
		"Танзания",
		"Украина",
		"Уганда",
		"Малые Тихоокеанские отдаленные острова Соединенных Штатов",
		"Соединенные Штаты Америки",
		"Уругвай",
		"Узбекистан",
		"Папский Престол (Ватикан)",
		"Сент-Винсент и Гренадины",
		"Венесуэла",
		"Виргинские острова (Британские)",
		"Виргинские острова (США)",
		"Вьетнам",
		"Вануату",
		"Уоллис и Футуна",
		"Самоа",
		"Йемен",
		"Майотта",
		"Южная Африка",
		"Замбия",
		"Зимбабве",

	};
}
