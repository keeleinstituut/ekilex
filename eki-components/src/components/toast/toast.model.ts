export interface Toast {
  title?: string;
  body: string;
  readMoreUrl?: string;
  readMoreText?: string;
  readMoreIsExternal?: boolean;
  closeLabel: string;
  isVisible?: boolean;
  id?: number;
  type?: 'error' | 'success';
  class: string;
}
