interface EkiUserPermission {
  id: number;
  name: string;
  email: string;
  apiKeyExists: boolean;
  apiCrud: boolean;
  admin: boolean;
  master: boolean;
  enabled: boolean;
  reviewComment: string | null;
  createdOn: string;
  enablePending: boolean;
  applications: unknown[];
  datasetPermissions: unknown[];
}

export interface UserFilter {
  userNameFilter: string;
  userPermDatasetCodeFilter: string | null;
  userEnablePendingFilter: string | null;
  orderBy: string;
  ekiUserPermissions: EkiUserPermission[];
}
