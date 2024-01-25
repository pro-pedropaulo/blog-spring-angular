import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'translateDate',
  standalone: true
})
export class TranslateDatePipe implements PipeTransform {

  transform(value: string): string {
    if (value === null) {
        return ''; 
      }

      const daysMap: { [key: string]: string } = {
      'Sunday': 'Domingo',
      'Monday': 'Segunda-feira',
      'Tuesday': 'Terça-feira',
      'Wednesday': 'Quarta-feira',
      'Thursday': 'Quinta-feira',
      'Friday': 'Sexta-feira',
      'Saturday': 'Sábado'
    };

    let translatedValue = value;
    Object.keys(daysMap).forEach(day => {
      translatedValue = translatedValue.replace(day, daysMap[day]);
    });

    return translatedValue;
  }
}
