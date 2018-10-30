package com.transform.work.test;

import com.transform.util.StrUtils;

/**
 * Created by tianhc on 2018/10/30.
 */
public class DiffTableField {

    public static void user() {
        // uas-tables.xml
        String[] str1 = {"id",
                "org_info_id",
                "org_dept_id",
                "primary_account",
                "username",
                "ca_cert",
                "general_name",
                "ca_express",
                "realname",
                "personid",
                "userpwd",
                "status",
                "loginmode",
                "mobile",
                "userpwd_update_time",
                "latest_login_time",
                "kt_opno",
                "kt_pki",
                "kt_pre_login_time",
                "last_login_ip",
                "kt_pre_login_ip",
                "kt_login_times",
                "kt_op_limit",
                "kt_secrecy",
                "kt_op_type",
                "email",
                "kt_data_network",
                "kt_unitid_id",
                "link_man",
                "link_tel",
                "kt_is_business",
                "kt_project_id",
                "kt_private_key",
                "kt_is_activation",
                "reasons_disable",
                "notes",
                "ts_notes",
                "ts_deal_flag"};
        // dev0 uas_org_user
        String[] str2 = {"id",
                "org_info_id",
                "org_dept_id",
                "username",
                "realname",
                "userpwd",
                "status",
                "mobile",
                "notes",
                "personID",
                "create_time",
                "modify_time",
                "loginmode",
                "ca_cert",
                "userpwd_update_time",
                "latest_login_time",
                "general_name",
                "ca_express",
                "primary_account",
                "sys_task_id",
                "kt_opno",
                "kt_pki",
                "kt_pre_login_time",
                "last_login_ip",
                "kt_pre_login_ip",
                "kt_login_times",
                "kt_op_limit",
                "kt_secrecy",
                "kt_op_type",
                "email",
                "kt_data_network",
                "kt_unitid_id",
                "link_man",
                "link_tel",
                "kt_is_business",
                "kt_project_id",
                "kt_private_key",
                "kt_is_activation",
                "reasons_disable",
                "ts_notes",
                "ts_deal_flag"};
        StrUtils.diffInCollection("user",str1,str2);
    }

    public static void orginfo() {
        String[] str1 = {"id",
                "settle_center_id",
                "supervise_id",
                "code",
                "name",
                "type",
                "legal_person",
                "org_short_name",
                "supervise_area_id",
                "supervise_area",
                "supervise_med_org",
                "contact_address",
                "base_area_id",
                "locate_area",
                "fax",
                "fixed_telephone",
                "email",
                "audit_status",
                "audit_desc",
                "valid_status",
                "forbid_status",
                "own_apps",
                "notes",
                "credit",
                "buz_licence_file",
                "organization_file",
                "tax_file",
                "auth_file",
                "product_cert_num",
                "business_cert_num",
                "three_cert_in_one",
                "business_start_time",
                "business_end_time",
                "legal_person_mobile",
                "legal_person_idcard",
                "enterprise_type",
                "found_date",
                "product_cert_end_date",
                "business_cert_end_date",
                "register_funds",
                "register_address",
                "product_address",
                "website_link",
                "postal_code",
                "sed_class_Consumables",
                "product_cert_file",
                "bus_cert_file",
                "application_file",
                "other_ref_cert_file",
                "auth_person_idcard_file",
                "license_number",
                "license_file",
                "hospital_type",
                "hospital_base",
                "hospital_level",
                "hospital_kind",
                "kt_org_id",
                "kt_code",
                "kt_audit_status",
                "organization_code",
                "short_pinyin",
                "kt_region_code",
                "kt_is_province",
                "kt_enterprise_type",
                "kt_licence",
                "link_person",
                "link_person_mobile",
                "deleted",
                "oversea",
                "kt_data_network",
                "kt_dis_range",
                "kt_bak_link_person",
                "kt_bak_link_person_mobile",
                "kt_product_cert_file",
                "kt_combined",
                "kt_combined_name",
                "kt_last_update_cgzx",
                "kt_combined_id",
                "product_category",
                "legal_person_idcard_file",
                "social_insurance_file",
                "authorization_file",
                "authorization_cert_file",
                "kt_commitment_file",
                "auth_person_idcard",
                "kt_enttype",
                "auth_person_name",
                "auth_person_mobile",
                "kt_cgzx_notes",
                "kt_sup_level",
                "ts_notes",
                "ts_deal_flag",
                "isFileConvert"};
        String[] str2 = {"id",
                "code",
                "name",
                "audit_status",
                "audit_desc",
                "valid_status",
                "forbid_status",
                "own_apps",
                "notes",
                "legal_person",
                "credit",
                "buz_licence_file",
                "organization_file",
                "tax_file",
                "auth_file",
                "license_number",
                "license_file",
                "create_time",
                "modify_time",
                "type",
                "org_short_name",
                "supervise_area_id",
                "contact_address",
                "locate_area",
                "fax",
                "product_cert_num",
                "business_cert_num",
                "three_cert_in_one",
                "business_start_time",
                "business_end_time",
                "legal_person_mobile",
                "legal_person_idcard",
                "enterprise_type",
                "found_date",
                "product_cert_end_date",
                "business_cert_end_date",
                "register_funds",
                "register_address",
                "product_address",
                "website_link",
                "postal_code",
                "sed_class_Consumables",
                "product_cert_file",
                "bus_cert_file",
                "other_ref_cert_file",
                "auth_person_idcard_file",
                "hospital_type",
                "hospital_level",
                "hospital_kind",
                "settle_center_id",
                "supervise_id",
                "supervise_med_org",
                "fixed_telephone",
                "hospital_base",
                "sys_task_id",
                "supervise_area",
                "base_area_id",
                "email",
                "application_file",
                "kt_org_id",
                "kt_code",
                "kt_audit_status",
                "organization_code",
                "short_pinyin",
                "kt_region_code",
                "kt_is_province",
                "kt_enterprise_type",
                "kt_licence",
                "link_person",
                "link_person_mobile",
                "deleted",
                "oversea",
                "kt_data_network",
                "kt_dis_range",
                "kt_bak_link_person",
                "kt_bak_link_person_mobile",
                "kt_product_cert_file",
                "kt_combined",
                "kt_combined_name",
                "kt_last_update_cgzx",
                "kt_combined_id",
                "product_category",
                "legal_person_idcard_file",
                "social_insurance_file",
                "authorization_file",
                "authorization_cert_file",
                "kt_commitment_file",
                "auth_person_idcard",
                "kt_enttype",
                "auth_person_name",
                "auth_person_mobile",
                "kt_cgzx_notes",
                "kt_sup_level",
                "ts_notes",
                "ts_deal_flag",
                "isFileConvert"};
        StrUtils.diffInCollection("orginfo",str1,str2);
    }

    public static void orgApply() {
        String[] str1 = {"id",
                "org_info_id",
                "settle_center_id",
                "supervise_id",
                "code",
                "name",
                "type",
                "legal_person",
                "org_short_name",
                "supervise_area_id",
                "supervise_area",
                "supervise_med_org",
                "contact_address",
                "base_area_id",
                "locate_area",
                "fax",
                "fixed_telephone",
                "email",
                "audit_status",
                "audit_desc",
                "audit_desc_file",
                "valid_status",
                "forbid_status",
                "own_apps",
                "notes",
                "credit",
                "buz_licence_file",
                "organization_file",
                "tax_file",
                "auth_file",
                "product_cert_num",
                "business_cert_num",
                "three_cert_in_one",
                "business_start_time",
                "business_end_time",
                "legal_person_mobile",
                "legal_person_idcard",
                "enterprise_type",
                "found_date",
                "product_cert_end_date",
                "business_cert_end_date",
                "register_funds",
                "register_address",
                "product_address",
                "website_link",
                "postal_code",
                "sed_class_Consumables",
                "product_cert_file",
                "bus_cert_file",
                "application_file",
                "other_ref_cert_file",
                "auth_person_idcard_file",
                "license_number",
                "license_file",
                "hospital_type",
                "hospital_base",
                "hospital_level",
                "hospital_kind",
                "organization_file_audit",
                "tax_file_audit",
                "auth_file_audit",
                "license_file_audit",
                "kt_org_id",
                "kt_code",
                "organization_code",
                "short_pinyin",
                "kt_region_code",
                "kt_is_province",
                "kt_enterprise_type",
                "kt_licence",
                "link_person",
                "link_person_mobile",
                "deleted",
                "oversea",
                "kt_data_network",
                "kt_dis_range",
                "kt_bak_link_person",
                "kt_bak_link_person_mobile",
                "kt_product_cert_file",
                "kt_combined",
                "kt_combined_name",
                "kt_last_update_cgzx",
                "kt_combined_id",
                "product_category",
                "legal_person_idcard_file",
                "social_insurance_file",
                "authorization_file",
                "authorization_cert_file",
                "kt_commitment_file",
                "auth_person_idcard",
                "kt_enttype",
                "auth_person_name",
                "auth_person_mobile",
                "kt_cgzx_notes",
                "kt_sup_level",
                "ts_notes",
                "ts_deal_flag",
                "isFileConvert",
                "change_time",
                "audit_time",
                "audit_person",
                "realname",
                "personid",
                "mobile"};
        String[] str2 = {"id",
                "org_info_id",
                "name",
                "audit_status",
                "audit_desc",
                "notes",
                "create_time",
                "modify_time",
                "legal_person",
                "credit",
                "buz_licence_file",
                "organization_file",
                "tax_file",
                "auth_file",
                "license_number",
                "license_file",
                "realname",
                "personid",
                "mobile",
                "change_time",
                "audit_time",
                "audit_person",
                "type",
                "sys_task_id",
                "settle_center_id",
                "supervise_id",
                "code",
                "org_short_name",
                "supervise_area_id",
                "supervise_area",
                "supervise_med_org",
                "contact_address",
                "base_area_id",
                "locate_area",
                "fax",
                "fixed_telephone",
                "email",
                "audit_desc_file",
                "valid_status",
                "forbid_status",
                "own_apps",
                "product_cert_num",
                "business_cert_num",
                "three_cert_in_one",
                "business_start_time",
                "business_end_time",
                "legal_person_mobile",
                "legal_person_idcard",
                "enterprise_type",
                "found_date",
                "product_cert_end_date",
                "business_cert_end_date",
                "register_funds",
                "register_address",
                "product_address",
                "website_link",
                "postal_code",
                "sed_class_Consumables",
                "product_cert_file",
                "bus_cert_file",
                "application_file",
                "other_ref_cert_file",
                "auth_person_idcard_file",
                "hospital_type",
                "hospital_base",
                "hospital_level",
                "hospital_kind",
                "organization_file_audit",
                "tax_file_audit",
                "auth_file_audit",
                "license_file_audit",
                "kt_org_id",
                "kt_code",
                "organization_code",
                "short_pinyin",
                "kt_region_code",
                "kt_is_province",
                "kt_enterprise_type",
                "kt_licence",
                "link_person",
                "link_person_mobile",
                "deleted",
                "oversea",
                "kt_data_network",
                "kt_dis_range",
                "kt_bak_link_person",
                "kt_bak_link_person_mobile",
                "kt_product_cert_file",
                "kt_combined",
                "kt_combined_name",
                "kt_last_update_cgzx",
                "kt_combined_id",
                "product_category",
                "legal_person_idcard_file",
                "social_insurance_file",
                "authorization_file",
                "authorization_cert_file",
                "kt_commitment_file",
                "auth_person_idcard",
                "kt_enttype",
                "auth_person_name",
                "auth_person_mobile",
                "kt_cgzx_notes",
                "kt_sup_level",
                "ts_notes",
                "ts_deal_flag",
                "isFileConvert"};
        StrUtils.diffInCollection("orgApply",str1,str2);
    }

    public static void orgApplyHis() {
        String[] str1 = {};
        String[] str2 = {};
        StrUtils.diffInCollection("orgApplyHis",str1,str2);
    }

    public static void main(String[] args) {
        //user();
        //orginfo();
        orgApply();
        //orgApplyHis();
    }

}