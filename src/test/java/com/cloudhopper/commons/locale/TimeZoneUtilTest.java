/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.cloudhopper.commons.locale;

// java imports
import com.cloudhopper.commons.locale.TimeZoneUtil;
import com.cloudhopper.commons.locale.TimeZone;
import java.io.IOException;

// third party imports
import java.util.List;
import org.junit.*;
import org.apache.log4j.Logger;

// my imports

/**
 *
 * @author joelauer
 */
public class TimeZoneUtilTest {

    private static final Logger logger = Logger.getLogger(TimeZoneUtilTest.class);

    @Test
    public void continentalTimezones() throws Exception {
        List<TimeZone> timezones = TimeZoneUtil.getContinentalTimezones();
        // UTC should always be first
        Assert.assertEquals("UTC", timezones.get(0).getId());
        // make sure timezones are either included or excluded from this list
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Abidjan")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Accra")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Addis_Ababa")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Algiers")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Asmara")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Asmera")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Bamako")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Bangui")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Banjul")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Bissau")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Blantyre")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Brazzaville")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Bujumbura")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Cairo")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Casablanca")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Ceuta")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Conakry")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Dakar")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Dar_es_Salaam")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Djibouti")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Douala")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/El_Aaiun")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Freetown")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Gaborone")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Harare")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Johannesburg")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Kampala")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Khartoum")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Kigali")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Kinshasa")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Lagos")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Libreville")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Lome")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Luanda")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Lubumbashi")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Lusaka")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Malabo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Maputo")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Maseru")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Mbabane")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Mogadishu")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Monrovia")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Nairobi")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Ndjamena")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Niamey")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Nouakchott")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Ouagadougou")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Porto-Novo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Sao_Tome")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Timbuktu")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Tripoli")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Tunis")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Africa/Windhoek")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Adak")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Anchorage")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Anguilla")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Antigua")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Araguaina")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/Buenos_Aires")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/Catamarca")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/ComodRivadavia")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/Cordoba")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/Jujuy")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/La_Rioja")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/Mendoza")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/Rio_Gallegos")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/Salta")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/San_Juan")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/San_Luis")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/Tucuman")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Argentina/Ushuaia")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Aruba")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Asuncion")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Atikokan")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Atka")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Bahia")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Barbados")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Belem")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Belize")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Blanc-Sablon")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Boa_Vista")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Bogota")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Boise")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Buenos_Aires")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Cambridge_Bay")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Campo_Grande")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Cancun")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Caracas")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Catamarca")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Cayenne")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Cayman")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Chicago")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Chihuahua")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Coral_Harbour")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Cordoba")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Costa_Rica")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Cuiaba")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Curacao")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Danmarkshavn")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Dawson")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Dawson_Creek")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Denver")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Detroit")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Dominica")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Edmonton")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Eirunepe")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/El_Salvador")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Ensenada")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Fort_Wayne")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Fortaleza")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Glace_Bay")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Godthab")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Goose_Bay")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Grand_Turk")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Grenada")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Guadeloupe")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Guatemala")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Guayaquil")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Guyana")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Halifax")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Havana")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Hermosillo")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Indiana/Indianapolis")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Indiana/Knox")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Indiana/Marengo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Indiana/Petersburg")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Indiana/Tell_City")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Indiana/Vevay")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Indiana/Vincennes")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Indiana/Winamac")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Indianapolis")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Inuvik")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Iqaluit")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Jamaica")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Jujuy")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Juneau")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Kentucky/Louisville")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Kentucky/Monticello")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Knox_IN")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/La_Paz")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Lima")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Los_Angeles")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Louisville")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Maceio")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Managua")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Manaus")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Marigot")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Martinique")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Mazatlan")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Mendoza")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Menominee")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Merida")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Mexico_City")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Miquelon")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Moncton")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Monterrey")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Montevideo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Montreal")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Montserrat")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Nassau")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/New_York")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Nipigon")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Nome")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Noronha")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/North_Dakota/Center")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/North_Dakota/New_Salem")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Panama")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Pangnirtung")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Paramaribo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Phoenix")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Port-au-Prince")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Port_of_Spain")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Porto_Acre")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Porto_Velho")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Puerto_Rico")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Rainy_River")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Rankin_Inlet")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Recife")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Regina")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Resolute")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Rio_Branco")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Rosario")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Santarem")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Santiago")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Santo_Domingo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Sao_Paulo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Scoresbysund")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Shiprock")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/St_Barthelemy")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/St_Johns")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/St_Kitts")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/St_Lucia")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/St_Thomas")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/St_Vincent")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Swift_Current")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Tegucigalpa")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Thule")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Thunder_Bay")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Tijuana")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Toronto")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Tortola")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Vancouver")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Virgin")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Whitehorse")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Winnipeg")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Yakutat")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("America/Yellowknife")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Antarctica/Casey")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Antarctica/Davis")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Antarctica/DumontDUrville")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Antarctica/Mawson")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Antarctica/McMurdo")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Antarctica/Palmer")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Antarctica/Rothera")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Antarctica/South_Pole")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Antarctica/Syowa")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Antarctica/Vostok")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Arctic/Longyearbyen")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Aden")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Almaty")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Amman")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Anadyr")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Aqtau")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Aqtobe")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Ashgabat")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Ashkhabad")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Baghdad")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Bahrain")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Baku")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Bangkok")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Beirut")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Bishkek")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Brunei")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Calcutta")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Choibalsan")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Chongqing")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Chungking")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Colombo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Dacca")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Damascus")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Dhaka")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Dili")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Dubai")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Dushanbe")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Gaza")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Harbin")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Ho_Chi_Minh")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Hong_Kong")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Hovd")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Irkutsk")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Istanbul")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Jakarta")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Jayapura")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Jerusalem")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Kabul")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Kamchatka")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Karachi")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Kashgar")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Katmandu")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Kolkata")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Krasnoyarsk")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Kuala_Lumpur")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Kuching")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Kuwait")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Macao")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Macau")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Magadan")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Makassar")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Manila")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Muscat")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Nicosia")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Novosibirsk")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Omsk")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Oral")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Phnom_Penh")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Pontianak")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Pyongyang")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Qatar")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Qyzylorda")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Rangoon")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Riyadh")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Saigon")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Sakhalin")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Samarkand")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Seoul")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Shanghai")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Singapore")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Taipei")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Tashkent")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Tbilisi")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Tehran")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Tel_Aviv")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Thimbu")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Thimphu")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Tokyo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Ujung_Pandang")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Ulaanbaatar")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Ulan_Bator")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Urumqi")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Vientiane")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Vladivostok")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Yakutsk")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Yekaterinburg")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Asia/Yerevan")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Atlantic/Azores")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Atlantic/Bermuda")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Atlantic/Canary")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Atlantic/Cape_Verde")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Atlantic/Faeroe")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Atlantic/Faroe")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Atlantic/Jan_Mayen")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Atlantic/Madeira")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Atlantic/Reykjavik")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Atlantic/South_Georgia")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Atlantic/St_Helena")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Atlantic/Stanley")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/ACT")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Adelaide")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Brisbane")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Broken_Hill")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Canberra")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Currie")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Darwin")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Eucla")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Hobart")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/LHI")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Lindeman")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Lord_Howe")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Melbourne")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/NSW")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/North")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Perth")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Queensland")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/South")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Sydney")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Tasmania")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Victoria")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/West")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Australia/Yancowinna")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Brazil/Acre")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Brazil/DeNoronha")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Brazil/East")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Brazil/West")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("CET")));

        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("CST6CDT")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Canada/Atlantic")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Canada/Central")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Canada/East-Saskatchewan")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Canada/Eastern")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Canada/Mountain")));

        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Canada/Newfoundland")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Canada/Pacific")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Canada/Saskatchewan")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Canada/Yukon")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Chile/Continental")));
        Assert.assertFalse(timezones.contains(TimeZoneUtil.getTimeZone("Chile/EasterIsland")));

        /**
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Cuba")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("EET")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("EST")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("EST5EDT")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Egypt")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Eire")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+0")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+1")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+10")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+11")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+12")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+2")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+3")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+4")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+5")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+6")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+7")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+8")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT+9")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-0")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-1")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-10")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-11")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-12")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-13")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-14")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-2")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-3")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-4")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-5")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-6")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-7")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-8")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT-9")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/GMT0")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/Greenwich")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/UCT")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/UTC")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/Universal")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Etc/Zulu")));
         */
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Amsterdam")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Andorra")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Athens")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Belfast")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Belgrade")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Berlin")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Bratislava")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Brussels")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Bucharest")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Budapest")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Chisinau")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Copenhagen")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Dublin")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Gibraltar")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Guernsey")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Helsinki")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Isle_of_Man")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Istanbul")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Jersey")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Kaliningrad")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Kiev")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Lisbon")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Ljubljana")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/London")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Luxembourg")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Madrid")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Malta")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Mariehamn")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Minsk")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Monaco")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Moscow")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Nicosia")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Oslo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Paris")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Podgorica")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Prague")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Riga")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Rome")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Samara")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/San_Marino")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Sarajevo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Simferopol")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Skopje")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Sofia")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Stockholm")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Tallinn")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Tirane")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Tiraspol")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Uzhgorod")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Vaduz")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Vatican")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Vienna")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Vilnius")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Volgograd")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Warsaw")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Zagreb")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Zaporozhye")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Europe/Zurich")));
        /**
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("GB")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("GB-Eire")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("GMT")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("GMT+0")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("GMT-0")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("GMT0")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Greenwich")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("HST")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Hongkong")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Iceland")));
         */
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Indian/Antananarivo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Indian/Chagos")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Indian/Christmas")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Indian/Cocos")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Indian/Comoro")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Indian/Kerguelen")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Indian/Mahe")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Indian/Maldives")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Indian/Mauritius")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Indian/Mayotte")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Indian/Reunion")));

        /**
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Iran")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Israel")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Jamaica")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Japan")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Kwajalein")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Libya")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("MET")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("MST")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("MST7MDT")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Mexico/BajaNorte")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Mexico/BajaSur")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Mexico/General")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("NZ")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("NZ-CHAT")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Navajo")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("PRC")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("PST8PDT")));
         */
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Apia")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Auckland")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Chatham")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Easter")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Efate")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Enderbury")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Fakaofo")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Fiji")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Funafuti")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Galapagos")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Gambier")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Guadalcanal")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Guam")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Honolulu")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Johnston")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Kiritimati")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Kosrae")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Kwajalein")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Majuro")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Marquesas")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Midway")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Nauru")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Niue")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Norfolk")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Noumea")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Pago_Pago")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Palau")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Pitcairn")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Ponape")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Port_Moresby")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Rarotonga")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Saipan")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Samoa")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Tahiti")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Tarawa")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Tongatapu")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Truk")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Wake")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Wallis")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Pacific/Yap")));
        /**
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Poland")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Portugal")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("ROC")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("ROK")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Singapore")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Turkey")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("UCT")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/Alaska")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/Aleutian")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/Arizona")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/Central")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/East-Indiana")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/Eastern")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/Hawaii")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/Indiana-Starke")));

        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/Michigan")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/Mountain")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/Pacific")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/Pacific-New")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("US/Samoa")));
         */
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("UTC")));

        /**
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Universal")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("W-SU")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("WET")));
        Assert.assertTrue(timezones.contains(TimeZoneUtil.getTimeZone("Zulu")));
         */
    }
    
}
