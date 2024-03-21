import localDE from '@angular/common/locales/de';
import localEN from '@angular/common/locales/en';
import localES from '@angular/common/locales/es';
import localFR from '@angular/common/locales/fr';
import localNL from '@angular/common/locales/nl';
import localJA from '@angular/common/locales/ja';
import { TranslateLoader } from "@ngx-translate/core";
import { Observable, of } from 'rxjs';
import cz from 'src/assets/i18n/cz.json';
import de from 'src/assets/i18n/de.json';
import en from 'src/assets/i18n/en.json';
import es from 'src/assets/i18n/es.json';
import fr from 'src/assets/i18n/fr.json';
import nl from 'src/assets/i18n/nl.json';
import ja from 'src/assets/i18n/ja.json';

export class MyTranslateLoader implements TranslateLoader {

    public getTranslation(key: string): Observable<any> {
        var language = Language.getByKey(key);
        if (language) {
            return of(language.json);
        }
        return of(Language.DEFAULT.json);
    }
}

export class Language {

    public static readonly DE: Language = new Language("German", "de", "de", de, localDE);
    public static readonly EN: Language = new Language("English", "en", "en", en, localEN);
    public static readonly CZ: Language = new Language("Czech", "cz", "de", cz, localDE /* NOTE: there is no locale in @angular/common for Czech */);
    public static readonly NL: Language = new Language("Dutch", "nl", "nl", nl, localNL);
    public static readonly ES: Language = new Language("Spanish", "es", "es", es, localES);
    public static readonly FR: Language = new Language("French", "fr", "fr", fr, localFR);
    public static readonly JA: Language = new Language("Japanese", "ja", "ja", ja, localJA);

    public static readonly ALL = [Language.DE, Language.EN, Language.CZ, Language.NL, Language.ES, Language.FR, Language.JA];
    public static readonly DEFAULT = Language.DE;

    public static getByKey(key: string): Language | null {
        for (let language of Language.ALL) {

            if (language.key == key) {
                return language;
            }
        }
        return null;
    }

    public static getByBrowserLang(browserLang: string): Language | null {
        switch (browserLang) {
            case "de": return Language.DE;
            case "en":
            case "en-US":
                return Language.EN;
            case "es": return Language.ES;
            case "nl": return Language.NL;
            case "cz": return Language.CZ;
            case "fr": return Language.FR;
            case "ja": return Language.JA;
            default: return null;
        }
    }

    public static getLocale(language: string) {
        switch (language) {
            case Language.DE.key: return Language.DE.locale;
            case Language.EN.key: return Language.EN.locale;
            case Language.ES.key: return Language.ES.locale;
            case Language.NL.key: return Language.NL.locale;
            case Language.CZ.key: return Language.CZ.locale;
            case Language.FR.key: return Language.FR.locale;
            case Language.JA.key: return Language.JA.locale;
            default: return Language.DEFAULT.locale;
        }
    }

    constructor(
        public readonly title: string,
        public readonly key: string,
        public readonly i18nLocaleKey: string,
        public readonly json: any,
        public readonly locale: any,
    ) {
    }
}
