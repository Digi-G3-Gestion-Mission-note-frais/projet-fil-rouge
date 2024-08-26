import { Component, Input } from '@angular/core';
import { RouterLink, RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@Component({
  standalone: true,
  imports: [FontAwesomeModule, RouterModule],
  providers: [RouterLink],
  selector: 'app-custom-button-link-component',
  templateUrl: './custom-button-link.component.html',
})
export class CustomButtonLinkComponent {
  @Input() text!: string;
  @Input() link?: string = '';
  @Input() textSize?: string = '';
  @Input() theme?: 'regular' | 'error' | 'border' = 'regular';
  @Input() iconLeft: any | undefined;
  @Input() iconRight: any | undefined;

  getRegularTheme(): string {
    let _theme = '';

    switch (this.theme) {
      case 'error':
        _theme = 'bg-tw_error text-tw_black';
        break;
      case 'border':
        _theme = 'text-tw_black border-2';
        break;
      default:
        _theme = 'bg-tw_black text-tw_white';
        break;
    }

    return _theme;
  }
}
