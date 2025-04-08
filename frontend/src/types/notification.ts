export interface Notification {
  id: number;
  userId: string;
  title: string;
  message: string;
  notificationTime: string;
  isEmailEnabled: boolean;
  isPushEnabled: boolean;
}

export interface NotificationsDto {
  id: number;
  userId: number;
  message: string;
  notificationTime: string;
  isRead: boolean;
}
