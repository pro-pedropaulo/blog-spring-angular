import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: 'translateMonth',
    standalone: true
  })
  export class TranslateMonthPipe implements PipeTransform {
    transform(value: string): string {
      const monthsMap: { [key: string]: string } = {
        'January': 'Janeiro',
        'February': 'Fevereiro',
        'March': 'Março',
        'April': 'Abril',
        'May': 'Maio',
        'June': 'Junho',
        'July': 'Julho',
        'August': 'Agosto',
        'September': 'Setembro',
        'October': 'Outubro',
        'November': 'Novembro',
        'December': 'Dezembro'
      };
  
      let translatedValue = value;
      Object.keys(monthsMap).forEach(month => {
        translatedValue = translatedValue.replace(month, monthsMap[month]);
      });
  
      return translatedValue;
    }
  }
  