import { PermissionCode } from '../../../core/constants/permissions';

export interface RoleResponse {
    id: number;
    name: string;
    permissions: PermissionCode[];
    assignable: boolean;
    systemRole: boolean;
}

export interface RolePermissionsUpdateRequest {
    permissions: PermissionCode[];
}
