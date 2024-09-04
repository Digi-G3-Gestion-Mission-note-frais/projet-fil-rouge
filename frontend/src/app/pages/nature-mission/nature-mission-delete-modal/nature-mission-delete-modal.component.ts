// import { Component, EventEmitter, Output } from '@angular/core';

// @Component({
//   selector: 'app-nature-mission-delete-modal',
//   template: `
//     <div class="modal">
//       <div class="modal-content">
//         <h3>Confirmation</h3>
//         <p>Es-tu sûr de vouloir supprimer cette mission ?</p>
//         <div class="modal-actions">
//           <button (click)="onConfirm()">Confirmer</button>
//           <button (click)="onCancel()">Annuler</button>
//         </div>
//       </div>
//     </div>
//   `,
//   styles: [`
//     .modal {
//       position: fixed;
//       top: 0;
//       left: 0;
//       width: 100%;
//       height: 100%;
//       background-color: rgba(0, 0, 0, 0.5);
//       display: flex;
//       justify-content: center;
//       align-items: center;
//     }
//     .modal-content {
//       background-color: white;
//       padding: 20px;
//       border-radius: 8px;
//       text-align: center;
//     }
//     .modal-actions {
//       display: flex;
//       justify-content: space-around;
//       margin-top: 20px;
//     }
//   `]
// })
// export class NatureMissionDeleteModalComponent {
//   @Output() confirm = new EventEmitter<void>();
//   @Output() cancel = new EventEmitter<void>();

//   onConfirm() {
//     this.confirm.emit();
//   }

//   onCancel() {
//     this.cancel.emit();
//   }
// }
